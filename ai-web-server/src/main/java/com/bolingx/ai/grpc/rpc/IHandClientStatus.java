package com.bolingx.ai.grpc.rpc;

import com.bolingx.ai.worker.grpc.core.ClientIdent;

public interface IHandClientStatus {

    void handClientStatusInfo(Throwable t, String msg, ClientIdent clientIdent);
}
