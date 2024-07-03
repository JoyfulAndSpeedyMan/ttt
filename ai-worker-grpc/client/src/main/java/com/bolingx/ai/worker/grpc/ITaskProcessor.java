package com.bolingx.ai.worker.grpc;

import com.bolingx.ai.worker.AiWorkerOuterClass;
import io.grpc.stub.StreamObserver;

public interface ITaskProcessor {

    void onTask(AiWorkerOuterClass.ServerRpcReq value, StreamObserver<AiWorkerOuterClass.ServerRpcCallback> serverStreamObserver);
}
