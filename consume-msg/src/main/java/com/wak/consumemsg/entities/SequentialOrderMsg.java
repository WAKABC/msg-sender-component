package com.wak.consumemsg.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wuankang
 * @Date 2024/10/31 17:02:02
 * @Description TODO 顺序消息
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class SequentialOrderMsg {
    /**
     * 订单 ID
     */
    private String orderId;
    /**
     * 类型
     */
    private String type;
}
