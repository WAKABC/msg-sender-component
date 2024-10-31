package com.wak.msgspringbootstarter.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuankang
 * @date 2024/9/23 16:01
 * @Description TODO
 * @Version 1.0
 */
@ConfigurationProperties(prefix = "thread.pool")
@Data
public class ThreadPoolProperties {
    private int corePoolSize;
    private int maxPoolSize;
    private int keepAliveTime;
    private int queueCapacity;
}
