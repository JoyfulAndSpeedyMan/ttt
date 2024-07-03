package com.bolingx.ai.worker.grpc;

import com.bolingx.ai.worker.AiWorkerOuterClass;

public interface IReportStatus {

    AiWorkerOuterClass.WorkerStatus getStatus();
}
