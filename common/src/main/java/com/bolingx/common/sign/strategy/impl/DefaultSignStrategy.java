package com.bolingx.common.sign.strategy.impl;

import com.bolingx.common.sign.strategy.AbstractSignStrategy;


public class DefaultSignStrategy extends AbstractSignStrategy {

    @Override
    public String getName() {
        return "default";
    }

    @Override
    protected String getEqualSymbol() {
        return "\\*(%@";
    }

    @Override
    protected String getSeparatorSymbol() {
        return "#\\$";
    }
}
