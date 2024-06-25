package com.bolingx.ai.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoUser {

    boolean required() default true;

    String message() default "当前未登录";

    String missUserMessage() default "当前未登录";
}
