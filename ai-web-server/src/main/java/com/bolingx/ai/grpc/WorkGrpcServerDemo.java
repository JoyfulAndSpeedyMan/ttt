package com.bolingx.ai.grpc;

import com.bolingx.ai.grpc.service.AIWorkerHolder;
import com.bolingx.ai.grpc.service.AIWorkerManager;
import com.bolingx.ai.grpc.service.ClientStatusListener;
import com.bolingx.ai.worker.AiWorkerOuterClass;
import com.bolingx.ai.worker.grpc.core.ClientIdent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class WorkGrpcServerDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        AIWorkerServer aiWorkerServer = new AIWorkerServer(9251);
        AIWorkerManager aiWorkerManager = aiWorkerServer.getAiWorkerManager();

        aiWorkerManager.setClientStatusListener(new ClientStatusListener() {
            @Override
            public void onReady(ClientIdent clientIdent, AIWorkerHolder holder) {
                holder.getServerRpc().sendRequest(
                        AiWorkerOuterClass.ServerRpcReq.newBuilder()
                                .setProcessorName("DemoProcess")
                                .setParams("demo params")
                                .build()
                )
                        .subscribe(result -> log.info("result: {}", result.toString()));
            }
        });



        aiWorkerServer.start();
        Thread.currentThread().join();
    }
}
