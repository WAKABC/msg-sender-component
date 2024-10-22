package com.wak.msgspringbootstarter.enums;

import lombok.Getter;

/**
 * @author wuankang
 * @date 2024/10/8 19:34
 * @Description 消息状态枚举
 * @Version 1.0
 */
@Getter
public enum MsgStatusEnum {
    INIT(0, "带投递"),
    SUCCESS(1, "投递成功"),
    FAIL(2, "投递失败");
    /**
     * 状态
     */
    private final Integer status;
    /**
     * 描述
     */
    private final String description;

    MsgStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }
}
