package com.bolingx.ai.grpc.service;

import com.bolingx.ai.grpc.rpc.ServerRpc;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class AIWorkerHolder {

    private String id;

    private ServerRpc serverRpc;
}
