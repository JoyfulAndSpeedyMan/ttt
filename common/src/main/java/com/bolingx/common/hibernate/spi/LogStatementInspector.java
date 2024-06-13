package com.bolingx.common.hibernate.spi;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;

@Slf4j
public class LogStatementInspector implements StatementInspector {

    @Override
    public String inspect(String s) {
        log.info(s);
        return s;
    }
}
