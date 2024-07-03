package com.bolingx.ai.worker.grpc.listen;

import com.google.protobuf.GeneratedMessageV3;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractListenServerProcessor<C extends GeneratedMessageV3, S extends GeneratedMessageV3> {

    private final Function<StreamObserver<C>, StreamObserver<S>> function;

    private final BiConsumer<Throwable, String> serverStreamErrorHandler;

    protected AtomicReference<StreamObserver<S>> ref = new AtomicReference<>(null);

    public AbstractListenServerProcessor(Function<StreamObserver<C>, StreamObserver<S>> function, BiConsumer<Throwable, String> serverStreamErrorHandler) {
        this.function = function;
        this.serverStreamErrorHandler = serverStreamErrorHandler;
    }

    public void startProcess() {
        StreamObserver<S> serverStreamObserver = function.apply(new StreamObserver<C>() {
            @Override
            public void onNext(C value) {
                getLog().info("{} onNext", getProcessorName());
                while (ref.get() == null) {
                    getLog().info("{} serverStreamObserver is null,自旋等待", getProcessorName());
                }
                onMessage(value, ref.get());
            }

            @Override
            public void onError(Throwable t) {
                serverStreamErrorHandler.accept(t, getProcessorName());
            }

            @Override
            public void onCompleted() {
                getLog().info("{} onCompleted", getProcessorName());
            }
        });
        ref.set(serverStreamObserver);
    }

    protected abstract String getProcessorName();

    protected abstract Logger getLog();

    protected abstract void onMessage(C value, StreamObserver<S> serverStreamObserver);
}
