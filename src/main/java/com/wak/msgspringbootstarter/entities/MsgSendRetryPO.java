package com.wak.msgspringbootstarter.entities;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wuankang
 * @date 2024/10/10 16:41
 * @Description 消息重试信息
 * @Version 1.0
 */
@Data
public class MsgSendRetryPO {
    /**
     * 是否重试
     */
    private Integer sendRetry;
    /**
     * 下次发送重试时间
     */
    private LocalDateTime nextSendRetryTime;
}
