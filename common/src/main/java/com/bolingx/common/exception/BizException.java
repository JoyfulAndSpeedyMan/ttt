package com.bolingx.common.exception;

import java.io.Serial;

public class BizException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4213590081997721737L;


    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }


    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
