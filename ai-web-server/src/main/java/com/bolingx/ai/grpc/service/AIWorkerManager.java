package com.bolingx.ai.grpc.service;

import com.bolingx.ai.worker.grpc.core.ClientIdent;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AIWorkerManager {

    @Getter
    private final Map<String, AIWorkerHolder> onlineWorker = new ConcurrentHashMap<>();

    @Setter
    private ClientStatusListener clientStatusListener;

    public AIWorkerHolder getWorker(String workerId) {
        return onlineWorker.get(workerId);
    }

    public void online(ClientIdent clientIdent, AIWorkerHolder aiWorkerHolder) {
        AIWorkerHolder oldHolder = onlineWorker.get(clientIdent.getId());
        onlineWorker.put(clientIdent.getId(), aiWorkerHolder);
        if (clientStatusListener != null) {
            clientStatusListener.online(clientIdent, aiWorkerHolder, oldHolder);
        }
    }

    public void ready(ClientIdent clientIdent) {
        if (clientStatusListener != null) {
            clientStatusListener.onReady(clientIdent, onlineWorker.get(clientIdent.getId()));
        }
    }

    public void offline(ClientIdent clientIdent) {
        if (clientStatusListener != null) {
            AIWorkerHolder holder = onlineWorker.remove(clientIdent.getId());
            clientStatusListener.offline(clientIdent, holder);
        }
    }
}
