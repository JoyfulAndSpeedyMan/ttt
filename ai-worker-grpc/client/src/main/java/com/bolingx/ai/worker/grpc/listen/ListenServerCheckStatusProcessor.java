package com.bolingx.ai.worker.grpc.listen;

import com.bolingx.ai.worker.AiWorkerOuterClass;
import com.bolingx.ai.worker.grpc.IReportStatus;
import io.grpc.stub.StreamObserver;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
public class ListenServerCheckStatusProcessor extends AbstractListenServerProcessor<
        AiWorkerOuterClass.WorkerCheck,
        AiWorkerOuterClass.WorkerStatus> {

    @Setter
    private IReportStatus iReportStatus;

    public ListenServerCheckStatusProcessor(Function<StreamObserver<AiWorkerOuterClass.WorkerCheck>, StreamObserver<AiWorkerOuterClass.WorkerStatus>> function, BiConsumer<Throwable, String> serverStreamErrorHandler) {
        super(function, serverStreamErrorHandler);
    }

    @Override
    protected String getProcessorName() {
        return "";
    }

    @Override
    protected Logger getLog() {
        return log;
    }

    @Override
    protected void onMessage(AiWorkerOuterClass.WorkerCheck value, StreamObserver<AiWorkerOuterClass.WorkerStatus> serverStreamObserver) {
        if(iReportStatus != null) {
            serverStreamObserver.onNext(iReportStatus.getStatus());
        }
    }

    public void offline() {
        ref.get().onNext(
                AiWorkerOuterClass.WorkerStatus.newBuilder()
                        .setStateOfLife(AiWorkerOuterClass.StateOfLife.STATE_OF_LIFE_OFFLINE)
                        .build()
        );
    }
}
