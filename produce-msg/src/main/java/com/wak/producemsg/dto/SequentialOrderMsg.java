package com.wak.producemsg.dto;

import lombok.Data;

/**
 * @author wuankang
 * @Date 2024/10/31 16:33:37
 * @Description TODO 顺序订单
 * @Version 1.0
 */
@Data
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
