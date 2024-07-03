package com.bolingx.ai.worker.grpc.core;

import io.grpc.Metadata;

public class GrpcHeader {
    public static final Metadata.Key<String> HEADER_KEY_ID =
            Metadata.Key.of("id", Metadata.ASCII_STRING_MARSHALLER);
}
