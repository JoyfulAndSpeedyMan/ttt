package com.bolingx.ai.security.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.session.redis")
@Getter
@Setter
public class SpringRedisSessionProperties extends RedisSessionProperties {
    private String host;

    private int port;

    private int database;
}
