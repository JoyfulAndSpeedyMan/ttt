package com.bolingx.ai.config.beans;

import com.bolingx.common.model.MessageHelper;
import com.bolingx.common.model.config.DebugConfig;
import com.bolingx.common.web.servlet.hepler.ResponseHelper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageBeans {

    @Bean
    @ConfigurationProperties("debug-config")
    public DebugConfig debugConfig() {
        return new DebugConfig();
    }

    @Bean
    public MessageHelper messageHelper(MessageSource messageSource) {
        return new MessageHelper(messageSource);
    }

    @Bean
    public ResponseHelper responseHelper(MessageSource messageSource, DebugConfig debugConfig){
        return new ResponseHelper(messageSource, debugConfig);
    }
}
