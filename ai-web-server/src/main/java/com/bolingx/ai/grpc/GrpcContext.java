package com.bolingx.ai.grpc;

import com.bolingx.ai.worker.grpc.core.ClientIdent;

public class GrpcContext {
    private static final ThreadLocal<ClientIdent> clientIdentThreadLocal = new ThreadLocal<>();

    public static ClientIdent getClientIdent() {
        return clientIdentThreadLocal.get();
    }

    public static void setClientIdent(ClientIdent clientIdent) {
        clientIdentThreadLocal.set(clientIdent);
    }

    public static String getClientId() {
        return getClientIdent().getId();
    }

    public static void removeClientIdent() {
        clientIdentThreadLocal.remove();
    }
}
