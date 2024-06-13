package com.bolingx.common.model;

import org.springframework.context.MessageSource;

import java.util.Locale;

public class MessageHelper {

    private final MessageSource messageSource;


    public MessageHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public <T> Message<T> success() {
        return this.of(Message.SUCCESS_CODE, null);
    }

    public <T> Message<T> success(T data) {
        return this.of(Message.SUCCESS_CODE, data);
    }

    public <T> Message<T> of(String code) {
        return this.of(code, null);
    }

    public <T> Message<T> of(String code, T data) {
        return new Message<T>(code, messageSource.getMessage(code, null, Locale.getDefault()), data);
    }
}
