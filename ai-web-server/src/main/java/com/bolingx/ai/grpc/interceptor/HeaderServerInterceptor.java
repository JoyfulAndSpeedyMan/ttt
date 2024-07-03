package com.bolingx.ai.grpc.interceptor;

import com.bolingx.ai.worker.grpc.core.ClientIdent;
import com.bolingx.ai.grpc.GrpcContext;
import com.bolingx.ai.worker.grpc.core.GrpcHeader;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeaderServerInterceptor implements ServerInterceptor {


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            final Metadata requestHeaders,
            ServerCallHandler<ReqT, RespT> next) {

        String id = requestHeaders.get(GrpcHeader.HEADER_KEY_ID);
        log.info("header id: {}", id);
        ClientIdent clientIdent = new ClientIdent(id);
        GrpcContext.setClientIdent(clientIdent);
        return next.startCall(call, requestHeaders);
    }

    private <ReqT, RespT> ServerCall.Listener<ReqT> close(ServerCall<ReqT, RespT> call) {
        Status status = Status.Code.INVALID_ARGUMENT.toStatus();
        call.close(status, new Metadata());
        return new ServerCall.Listener<ReqT>() {
            @Override
            public void onMessage(ReqT message) {
                super.onMessage(message);
            }
        };
    }
}
