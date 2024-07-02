package com.bolingx.ai.security.session;

import com.bolingx.ai.security.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;

@Configuration
public class SpringRedisSessionConfig extends RedisHttpSessionConfiguration {

    private ClassLoader loader;

    private SpringRedisSessionProperties properties;

    private RedisSerializer<Object> redisSerializer;

    private RedisConnectionFactory redisConnectionFactory;

    @PostConstruct
    public void init() {
        redisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper());
        redisConnectionFactory = springSessionRedisConnectionFactory();
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        return mapper;
    }

    public RedisConnectionFactory springSessionRedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(properties.getHost());
        redisStandaloneConfiguration.setPort(properties.getPort());
        redisStandaloneConfiguration.setDatabase(properties.getDatabase());
        LettuceConnectionFactory lettuceConnectionFactory
                = new LettuceConnectionFactory(redisStandaloneConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }


    @Override
    protected RedisConnectionFactory getRedisConnectionFactory() {
        return redisConnectionFactory;
    }

    @Override
    protected RedisSerializer<Object> getDefaultRedisSerializer() {
        return redisSerializer;
    }

    @Autowired
    public void setProperties(SpringRedisSessionProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setRedisConnectionFactory(ObjectProvider<RedisConnectionFactory> springSessionRedisConnectionFactory, ObjectProvider<RedisConnectionFactory> redisConnectionFactory) {
    }

    @Override
    public void setDefaultRedisSerializer(RedisSerializer<Object> defaultRedisSerializer) {
    }


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        super.setBeanClassLoader(classLoader);
        this.loader = classLoader;
    }


}
