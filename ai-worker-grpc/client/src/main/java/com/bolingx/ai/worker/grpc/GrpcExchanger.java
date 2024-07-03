package com.bolingx.ai.worker.grpc;

import com.bolingx.ai.worker.AiWorkerGrpc;
import com.bolingx.ai.worker.AiWorkerOuterClass;
import com.bolingx.ai.worker.grpc.listen.ListenServerCheckStatusProcessor;
import com.bolingx.ai.worker.grpc.listen.ListenServerRpcProcessor;
import com.google.protobuf.Empty;
import io.grpc.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.grpc.ConnectivityState.IDLE;
import static io.grpc.ConnectivityState.SHUTDOWN;

@Slf4j
public class GrpcExchanger {

    @Setter
    private String serverAddr;

    @Setter
    private int serverPort;

    @Setter
    private boolean security;

    @Setter
    private List<String> processors;

    @Setter
    private IReportStatus iReportStatus;

    @Setter
    private ITaskProcessor iTaskProcessor;

    private final AtomicInteger errorTimes = new AtomicInteger(0);

    final int maxErrorTimes = 3;

    private ManagedChannel originChannel;

    private Channel channel;

    private AiWorkerGrpc.AiWorkerStub asyncStub;

    private AiWorkerGrpc.AiWorkerBlockingStub blockingStub;

    private final byte CONNECT_STATE_DISCONNECT = -1;

    private final byte CONNECT_STATE_TRYING = 0;

    private final byte CONNECT_STATE_CONNECTED = 1;

    private transient byte connectState = CONNECT_STATE_DISCONNECT;

    private final BlockingQueue<Runnable> connectQueue = new ArrayBlockingQueue<>(1);

    private ScheduledThreadPoolExecutor scheduledExecutorService;

    private final HeaderClientInterceptor headerClientInterceptor = new HeaderClientInterceptor();

    private ListenServerCheckStatusProcessor listenServerCheckStatusProcessor;

    public GrpcExchanger(String serverAddr, int serverPort, List<String> processors) {
        this(serverAddr, serverPort, false, processors);
    }

    public GrpcExchanger(String serverAddr, int serverPort, boolean security, List<String> processors) {
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        this.security = security;
        this.processors = processors;
    }

    public void start() {
        ThreadPoolExecutor.DiscardOldestPolicy discardOldestPolicy = new ThreadPoolExecutor.DiscardOldestPolicy();
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1, discardOldestPolicy);
        reconnectServer();
    }

    public void connectServer() {
        ManagedChannel originChannel = null;
        try {
            log.info("connect grpc server {}:{}", serverAddr, serverPort);
            if (connectState == CONNECT_STATE_CONNECTED) {
                return;
            }
            this.connectState = CONNECT_STATE_TRYING;
            ChannelCredentials channelCredentials;
            if (security) {
                channelCredentials = TlsChannelCredentials.create();
            } else {
                channelCredentials = InsecureChannelCredentials.create();
            }
            originChannel = Grpc.newChannelBuilderForAddress(serverAddr, serverPort, channelCredentials)
                    .keepAliveTime(10, TimeUnit.SECONDS)
                    .keepAliveTimeout(10, TimeUnit.SECONDS)
                    .build();
            subscribeChannelStateChange(originChannel);

            Channel channel = ClientInterceptors.intercept(originChannel, headerClientInterceptor);

            AiWorkerGrpc.AiWorkerBlockingStub blockingStub = AiWorkerGrpc.newBlockingStub(originChannel);
            //noinspection ResultOfMethodCallIgnored
//            blockingStub.connectTest(Empty.newBuilder().build());
            register(blockingStub);
            this.originChannel = originChannel;
            this.channel = channel;
            this.blockingStub = blockingStub;
            this.asyncStub = AiWorkerGrpc.newStub(channel);
            this.errorTimes.set(0);
            this.connectState = CONNECT_STATE_CONNECTED;
            this.connectQueue.clear();
        } catch (Exception e) {
            if (originChannel != null) {
                closeChannel(originChannel);
            }
            connectState = CONNECT_STATE_DISCONNECT;
            throw e;
        }
    }

    protected void startProcessors(AiWorkerGrpc.AiWorkerStub asyncStub) {
        checkConnected();
        ListenServerCheckStatusProcessor listenServerCheckStatusProcessor
                = new ListenServerCheckStatusProcessor(asyncStub::listenServerCheckStatus, this::handServerStreamError);
        listenServerCheckStatusProcessor.setIReportStatus(iReportStatus);
        listenServerCheckStatusProcessor.startProcess();
        this.listenServerCheckStatusProcessor = listenServerCheckStatusProcessor;

        ListenServerRpcProcessor listenServerRpcProcessor = new ListenServerRpcProcessor(iTaskProcessor,
                asyncStub::listenServerRpc, this::handServerStreamError);
        listenServerRpcProcessor.startProcess();
    }

    protected void register(AiWorkerGrpc.AiWorkerBlockingStub blockingStub) {
        AiWorkerOuterClass.RegisterResponse response = blockingStub.register(AiWorkerOuterClass.RegisterRequest.newBuilder()
                .setVersion(1)
                .addAllProcessors(processors)
                .build());
        String id = response.getId();
        headerClientInterceptor.setId(id);
    }

    private void subscribeChannelStateChange(ManagedChannel channel) {
        ConnectivityState state = channel.getState(false);
        channel.notifyWhenStateChanged(state, () -> {
            log.info("channel state change {} -> {}", state.name(), channel.getState(false).name());
            if (channel.getState(false) == IDLE || channel.getState(false) == SHUTDOWN) {
                log.info("channel close, startReconnectTask");
                startReconnectTask();
            }
            subscribeChannelStateChange(channel);
        });
    }

    private void checkConnected() {
        if (connectState != CONNECT_STATE_CONNECTED) {
            startReconnectTask();
            throw new RuntimeException("当前链接不可用");
        }
    }

    private void startReconnectTask() {
        if (connectQueue.isEmpty()) {
            scheduledExecutorService.getQueue().clear();
            scheduledExecutorService.schedule(
                    this::reconnectServer,
                    5, TimeUnit.SECONDS);
        }
    }

    public void closeChannel(ManagedChannel channel) {
        try {
            boolean b = channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
            if (!b) {
                log.warn("closeChannel error");
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void reconnectServer() {
        try {
            log.info("reconnectServer");
            if (originChannel != null) {
                closeChannel(originChannel);
            }
            this.connectState = CONNECT_STATE_DISCONNECT;
            log.info("开始连接到grpc服务器");
            connectServer();
            log.info("连接成功，开始启动服务处理器");
            startProcessors(asyncStub);
            log.info("启动服务处理器成功");
            scheduledExecutorService.getQueue().clear();
        } catch (Exception e) {
            log.error("reconnectServer error {}:{}", serverAddr, serverPort, e);
            startReconnectTask();
        }
    }


    public void handServerStreamError(Throwable t, String msg) {
        Status status = Status.fromThrowable(t);
        Status.Code code = status.getCode();
        switch (code) {
            case UNAVAILABLE:
                startReconnectTask();
                break;
            case INTERNAL:
            default:
                log.error("{} onError", msg, t);
                try {
                    uploadOfflineStatus();
                } catch (Exception e) {
                    log.error("handServerStreamError uploadConnectStatus error", t);
                }
                error();
        }
    }

    private void uploadOfflineStatus() {
        if (listenServerCheckStatusProcessor != null) {
            listenServerCheckStatusProcessor.offline();
        }
    }

    private void error() {
        if (errorTimes.getAndIncrement() >= maxErrorTimes) {
            startReconnectTask();
        }
    }
}
