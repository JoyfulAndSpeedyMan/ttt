package com.bolingx.common.spring.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;

public class SingleStaticObjectProvider<T> implements ObjectProvider<T> {

    private T object;

    public SingleStaticObjectProvider(T object) {
        this.object = object;
    }

    @Override
    public T getObject(Object... args) throws BeansException {
        if (object == null) {
            throw new RuntimeException("没有object");
        }
        return object;
    }

    @Override
    public T getIfAvailable() throws BeansException {
        return object;
    }

    @Override
    public T getIfUnique() throws BeansException {
        if (object == null) {
            throw new RuntimeException("没有object");
        }
        return object;
    }

    @Override
    public T getObject() throws BeansException {        if (object == null) {
        throw new RuntimeException("没有object");
    }
        return object;
    }
}
