package com.bolingx.erp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import static org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME;


@Slf4j
@Configuration
public class VirtualExecutorConfig {

    @Bean
    public ThreadFactory virtualThreadFactory() {
        return Thread
                .ofVirtual()
                .name("vt-", 0)
                .factory();
    }

    @Bean
    public ExecutorService virtualExecutorService() {
        return Executors.newThreadPerTaskExecutor(virtualThreadFactory());
    }

    @Bean(name = {APPLICATION_TASK_EXECUTOR_BEAN_NAME,
            AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME})
    public AsyncTaskExecutor asyncTaskExecutor(ExecutorService virtualExecutorService) {
        return new TaskExecutorAdapter(virtualExecutorService);
    }

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer(
            ExecutorService virtualExecutorService) {
        return protocolHandler -> protocolHandler.setExecutor(virtualExecutorService);
    }
}
