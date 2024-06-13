package com.bolingx.common.hibernate.id.generator;

import com.bolingx.common.util.id.SnowFlake;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;

import java.lang.reflect.Member;

public class SnowFlakeIdGenerator implements IdentifierGenerator {

    private final SnowFlake snowFlake;

    public SnowFlakeIdGenerator(com.bolingx.common.hibernate.id.annotations.SnowFlake config,
                                Member annotatedMember,
                                CustomIdGeneratorCreationContext context){
        snowFlake = new SnowFlake();
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return snowFlake.nextId();
    }
}
