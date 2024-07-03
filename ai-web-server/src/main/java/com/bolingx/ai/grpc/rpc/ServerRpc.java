package com.bolingx.ai.grpc.rpc;

import com.bolingx.ai.worker.AiWorkerOuterClass;
import com.bolingx.ai.worker.grpc.core.ClientIdent;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Slf4j
public class ServerRpc {

    private final ClientIdent clientIdent;

    private final StreamObserver<AiWorkerOuterClass.ServerRpcReq> serverReqObserver;

    private final IHandClientStatus iHandClientStatus;

    private final AtomicLong reqIdGen = new AtomicLong();

    private final Map<Long, ServerRequest> requestMap = new ConcurrentHashMap<>();

    public ServerRpc(ClientIdent clientIdent,
                     StreamObserver<AiWorkerOuterClass.ServerRpcReq> serverReqObserver,
                     IHandClientStatus iHandClientStatus) {
        this.clientIdent = clientIdent;
        this.serverReqObserver = serverReqObserver;
        this.iHandClientStatus = iHandClientStatus;
    }

    public StreamObserver<AiWorkerOuterClass.ServerRpcCallback> createClientResponseObserver() {
        return new StreamObserver<>() {
            @Override
            public void onNext(AiWorkerOuterClass.ServerRpcCallback serverRpcCallback) {
                long reqId = serverRpcCallback.getReqId();
                ServerRequest serverRequest = requestMap.get(reqId);
                if (serverRequest == null) {
                    log.warn("未知的响应 {} {}", clientIdent.getId(), reqId);
                    return;
                }
                serverRequest.onMessage(serverRpcCallback);
            }

            @Override
            public void onError(Throwable throwable) {
                iHandClientStatus.handClientStatusInfo(throwable, "ServerRpc", clientIdent);
            }

            @Override
            public void onCompleted() {
                log.info("ServerRpc completed {}", clientIdent.getId());
            }
        };
    }

    public StreamObserver<AiWorkerOuterClass.ServerRpcCallback> createClientResponseObserverEmptyImpl() {
        return new StreamObserver<>() {
            @Override
            public void onNext(AiWorkerOuterClass.ServerRpcCallback value) {
                log.warn("listenServerRpc empty impl onNext {}", clientIdent.getId());
            }

            @Override
            public void onError(Throwable t) {
                log.warn("listenServerRpc empty impl onError {}", clientIdent.getId());
            }

            @Override
            public void onCompleted() {
                log.warn("listenServerRpc empty impl onCompleted {}", clientIdent.getId());
            }
        };
    }

    public Mono<AiWorkerOuterClass.ServerRpcCallback> sendRequest(AiWorkerOuterClass.ServerRpcReq serverRpcReq) {
        long reqId = reqIdGen.getAndIncrement();
        ServerRequest serverRequest = new ServerRequest(reqIdGen.getAndIncrement());
        requestMap.put(reqId, serverRequest);
        serverReqObserver.onNext(serverRpcReq);
        return serverRequest.getResult();
    }

    public Flux<AiWorkerOuterClass.ServerRpcCallback> sendStreamRequest(AiWorkerOuterClass.ServerRpcReq serverRpcReq) {
        long reqId = reqIdGen.getAndIncrement();
        ServerRequest serverRequest = new ServerRequest(reqId);
        requestMap.put(reqId, serverRequest);
        serverReqObserver.onNext(serverRpcReq);
        return serverRequest.getFluxResult();
    }
}
