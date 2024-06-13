package com.bolingx.common.hibernate.id.annotations;

import com.bolingx.common.hibernate.id.generator.SnowFlakeIdGenerator;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.id.enhanced.Optimizer;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@IdGeneratorType(SnowFlakeIdGenerator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface SnowFlake {
    Class<? extends Optimizer> optimizer() default Optimizer.class;
}
