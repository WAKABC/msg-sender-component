package com.wak.msgspringbootstarter.config;

import com.wak.msgspringbootstarter.config.properties.ThreadPoolProperties;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wuankang
 * @date 2024/9/23 16:01
 * @Description TODO 线程池配置类
 * @Version 1.0
 */
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadPoolConfiguration {
    @Resource
    private ThreadPoolProperties threadPoolProperties;

    @Bean
    @ConditionalOnProperty(value = "thread.pool")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveTime());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadNamePrefix("msg-thread-pool-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setPrestartAllCoreThreads(true);
        executor.initialize();
        return executor;
    }
}