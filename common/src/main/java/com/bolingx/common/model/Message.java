package com.bolingx.common.model;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Message<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public final static String SUCCESS_CODE = "20000";

    public final static String REQUEST_ERROR = "40000";

    public final static String SYSTEM_ERR_CODE = "50000";

    private String code;

    private String msg;

    private T data;

    protected Message(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public boolean codeIs(String code) {
        if (code == null) {
            return false;
        }
        return code.equals(this.code);
    }

    public boolean isSuccess() {
        return codeIs(SUCCESS_CODE);
    }

    public static <T> Message<T> of(String code, String msg, T data) {
        return new Message<T>(code, msg, data);
    }

    public static <T> Message<T> success(T data) {
        return of(SUCCESS_CODE, null, data);
    }

    public static Message<?> success() {
        return of(SUCCESS_CODE, null, null);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
