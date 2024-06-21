package com.bolingx.seller.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Configuration
public class VirtualExecutorConfig {
//
//    @Bean
//    public ThreadFactory virtualThreadFactory() {
//        return Thread
//                .ofVirtual()
//                .name("vt-", 0)
//                .factory();
//    }
//
//    @Bean
//    public ExecutorService virtualExecutorService() {
//        return Executors.newThreadPerTaskExecutor(virtualThreadFactory());
//    }
//
//    @Bean(name = {APPLICATION_TASK_EXECUTOR_BEAN_NAME,
//            AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME})
//    public AsyncTaskExecutor asyncTaskExecutor(ExecutorService virtualExecutorService) {
//        return new TaskExecutorAdapter(virtualExecutorService);
//    }
//
//    @Bean
//    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer(
//            ExecutorService virtualExecutorService) {
//        return protocolHandler -> protocolHandler.setExecutor(virtualExecutorService);
//    }
}
