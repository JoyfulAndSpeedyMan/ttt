package com.bolingx.ai.grpc.service;

import com.bolingx.ai.worker.grpc.core.ClientIdent;

public interface ClientStatusListener {

    default void online(ClientIdent clientIdent, AIWorkerHolder holder, AIWorkerHolder oldHolder) {
    }

    default void onReady(ClientIdent clientIdent, AIWorkerHolder holder) {
    }

    default void offline(ClientIdent clientIdent, AIWorkerHolder holder) {
    }

}
