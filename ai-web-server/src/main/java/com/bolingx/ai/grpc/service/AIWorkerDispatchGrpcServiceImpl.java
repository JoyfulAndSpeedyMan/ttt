package com.bolingx.ai.grpc.service;

import com.bolingx.ai.grpc.GrpcContext;
import com.bolingx.ai.grpc.rpc.ServerRpc;
import com.bolingx.ai.worker.AiWorkerGrpc;
import com.bolingx.ai.worker.AiWorkerOuterClass;
import com.bolingx.ai.worker.grpc.core.ClientIdent;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class AIWorkerDispatchGrpcServiceImpl extends AiWorkerGrpc.AiWorkerImplBase {

    private final AtomicLong counter = new AtomicLong();

    private final AIWorkerManager aiWorkerManager;


    public AIWorkerDispatchGrpcServiceImpl(AIWorkerManager aiWorkerManager) {
        this.aiWorkerManager = aiWorkerManager;
    }

    @Override
    public void register(AiWorkerOuterClass.RegisterRequest request, StreamObserver<AiWorkerOuterClass.RegisterResponse> responseObserver) {
        log.info("register {} {} {}", request.getVersion(), request.getProcessorsList(), request.getMaxParallel());
        String clientId = "ai-worker-" + counter.incrementAndGet();
        AIWorkerHolder aiWorkerHolder = new AIWorkerHolder();
        aiWorkerHolder.setId(clientId);

        aiWorkerManager.online(new ClientIdent(clientId), aiWorkerHolder);
        responseObserver.onNext(AiWorkerOuterClass.RegisterResponse.newBuilder()
                .setId(clientId)
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AiWorkerOuterClass.ServerRpcCallback> listenServerRpc(StreamObserver<AiWorkerOuterClass.ServerRpcReq> responseObserver) {
        ClientIdent clientIdent = GrpcContext.getClientIdent();
        log.info("grpc listenServerRpc {}", clientIdent.getId());
        ServerRpc serverRpc = new ServerRpc(clientIdent, responseObserver, this::handClientStatusInfo);
        AIWorkerHolder aiWorkerHolder = aiWorkerManager.getWorker(clientIdent.getId());
        if (aiWorkerHolder == null) {
            log.warn("listenServerRpc not online {}", clientIdent.getId());
            aiWorkerManager.offline(clientIdent);
            responseObserver.onError(new StatusRuntimeException(Status.CANCELLED));
            return serverRpc.createClientResponseObserverEmptyImpl();
        }
        aiWorkerHolder.setServerRpc(serverRpc);
        aiWorkerManager.ready(clientIdent);
        return serverRpc.createClientResponseObserver();
    }

    @Override
    public StreamObserver<AiWorkerOuterClass.WorkerStatus> listenServerCheckStatus(StreamObserver<AiWorkerOuterClass.WorkerCheck> responseObserver) {
        ClientIdent clientIdent = GrpcContext.getClientIdent();
        log.info("grpc listenServerCheckStatus {}", clientIdent.getId());
        return new StreamObserver<>() {
            @Override
            public void onNext(AiWorkerOuterClass.WorkerStatus value) {
                log.info("listenServerCheckStatus onNext {} {}", clientIdent.getId(), value.getStateOfLife().name());
            }

            @Override
            public void onError(Throwable t) {
                handClientStatusInfo(t, "listenServerCheckStatus", clientIdent);
            }

            @Override
            public void onCompleted() {
                log.info("listenServerCheckStatus onCompleted {}", clientIdent.getId());
            }
        };
    }

    public void handClientStatusInfo(Throwable t, String msg, ClientIdent clientIdent) {
        Status status = Status.fromThrowable(t);
        Status.Code code = status.getCode();
        if (code != Status.Code.OK) {
            aiWorkerManager.offline(clientIdent);
        }
        if (code == Status.Code.CANCELLED) {
            log.error("{} 下线 {} {}", clientIdent, msg, t.getMessage());
        } else {
            log.error("{} onError {} ", clientIdent.getId(), msg, t);
        }
    }
}
