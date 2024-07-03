package com.bolingx.ai.grpc;

import com.bolingx.ai.grpc.interceptor.HeaderServerInterceptor;
import com.bolingx.ai.grpc.service.AIWorkerDispatchGrpcServiceImpl;
import com.bolingx.ai.grpc.service.AIWorkerManager;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerInterceptors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AIWorkerServer {

    private final int port;

    private Server server;

    @Getter
    private AIWorkerManager aiWorkerManager = new AIWorkerManager();

    public AIWorkerServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .keepAliveTime(30, TimeUnit.MINUTES)
                .keepAliveTimeout(10, TimeUnit.SECONDS)
                .permitKeepAliveTime(7, TimeUnit.SECONDS)
                .addService(ServerInterceptors.intercept(
                        new AIWorkerDispatchGrpcServiceImpl(aiWorkerManager),
                        new HeaderServerInterceptor())
                )
                .build();
        server.start();
        log.info("Server started, listening on {}", port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                stop();
            } catch (InterruptedException e) {
                log.error("", e);
            }
            log.info("*** server shut down");
        }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
