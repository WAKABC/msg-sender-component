package com.wak.consumemsg.retry;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wuankang
 * @Date 2024/10/31 17:06:21
 * @Description 消息消费重试信息
 * @Version 1.0
 */
@Data
public class MsgConsumeRetryResult {
    /**
     * 是否重试
     */
    private boolean retry;
    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;
}
