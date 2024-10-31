package com.wak.msgspringbootstarter.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author wuankang
 * @date 2024/10/8 21:19
 * @description 消息延迟队列特性@version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "msg.delay.queue")
public class MsgDelayQueueProperties {
    /**
     * 延迟消息延迟队列容量
     */
    private int delayMsgDelayQueueCapacity = 10 * 1000;

    /**
     * 设置用于处理投递重试的延迟队列大小
     */
    private int delaySendRetryDelayQueueCapacity = 100 * 10000;
}
