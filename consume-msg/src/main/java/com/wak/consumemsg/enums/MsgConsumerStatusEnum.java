package com.wak.consumemsg.enums;

import lombok.Getter;

/**
 * @author wuankang
 * @Date 2024/10/31 17:05:21
 * @Description TODO 消息消费者地位枚举
 * @Version 1.0.0
 */
@Getter
public enum MsgConsumerStatusEnum {
    // 0 消费中 1 消费成功 2 消费失败
    INIT(0, "消费中"),
    SUCCESS(1, "消费成功"),
    FAIL(2, "消费失败");

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 描述
     */
    private final String description;

    MsgConsumerStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

}
