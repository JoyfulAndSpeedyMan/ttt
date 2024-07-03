package com.bolingx.ai.worker.grpc.listen;

import com.bolingx.ai.worker.AiWorkerGrpc;
import com.bolingx.ai.worker.AiWorkerOuterClass;
import com.bolingx.ai.worker.grpc.ITaskProcessor;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public class ListenServerRpcProcessor extends AbstractListenServerProcessor<
        AiWorkerOuterClass.ServerRpcReq,
        AiWorkerOuterClass.ServerRpcCallback> {

    private final ITaskProcessor iTaskProcessor;

    public ListenServerRpcProcessor(
            ITaskProcessor iTaskProcessor,
            Function<StreamObserver<AiWorkerOuterClass.ServerRpcReq>,
            StreamObserver<AiWorkerOuterClass.ServerRpcCallback>> function, BiConsumer<Throwable, String> serverStreamErrorHandler) {
        super(function, serverStreamErrorHandler);
        this.iTaskProcessor = iTaskProcessor;
    }

    @Override
    protected String getProcessorName() {
        return "ListenServerRpcProcessor";
    }

    @Override
    protected Logger getLog() {
        return log;
    }

    @Override
    protected void onMessage(AiWorkerOuterClass.ServerRpcReq value, StreamObserver<AiWorkerOuterClass.ServerRpcCallback> serverStreamObserver) {
        iTaskProcessor.onTask(value, serverStreamObserver);
    }
}
