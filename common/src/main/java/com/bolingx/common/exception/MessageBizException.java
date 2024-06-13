package com.bolingx.common.exception;

import lombok.Getter;

@Getter
public class MessageBizException extends BizException {

    private final String code;

    public MessageBizException(String code) {
        super((String) null);
        this.code = code;
    }

    public MessageBizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public MessageBizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public MessageBizException(String code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public MessageBizException(String code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

}
