package com.bolingx.seller.exception.user;

public class AutoUserException extends RuntimeException {
    public AutoUserException() {
    }

    public AutoUserException(String message) {
        super(message);
    }

    public AutoUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public AutoUserException(Throwable cause) {
        super(cause);
    }

    public AutoUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
