package com.bolingx.ai.grpc.rpc;


import com.bolingx.ai.worker.AiWorkerOuterClass;
import lombok.Getter;
import lombok.Setter;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;


public class ServerRequest {

    @Getter
    private final long reqId;

    @Getter
    private final boolean streamResult;

    @Setter
    private CorePublisher<AiWorkerOuterClass.ServerRpcCallback> result;

    private FluxSink<AiWorkerOuterClass.ServerRpcCallback> fluxSink;

    private MonoSink<AiWorkerOuterClass.ServerRpcCallback> monoSink;

    public ServerRequest(long reqId) {
        this(reqId, false);
    }

    public ServerRequest(long reqId, boolean streamResult) {
        this.reqId = reqId;
        this.streamResult = streamResult;
        if (streamResult) {
            result = Flux.create(sink -> {
                this.fluxSink = sink;
            });
        } else {
            result = Mono.create(sink -> {
                this.monoSink = sink;
            });
        }
    }

    public void onMessage(AiWorkerOuterClass.ServerRpcCallback callback) {
        if (streamResult) {
            fluxSink.next(callback);
            if (callback.getFlag() == 0) {
                fluxSink.complete();
            }
        } else {
            monoSink.success(callback);
        }
    }

    public Mono<AiWorkerOuterClass.ServerRpcCallback> getResult() {
        return (Mono<AiWorkerOuterClass.ServerRpcCallback>) result;
    }

    public CompletableFuture<AiWorkerOuterClass.ServerRpcCallback> getResultFuture() {
        return getResult().toFuture();
    }

    public Flux<AiWorkerOuterClass.ServerRpcCallback> getFluxResult() {
        return (Flux<AiWorkerOuterClass.ServerRpcCallback>) result;
    }
}
