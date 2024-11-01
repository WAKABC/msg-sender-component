package com.wak.producemsg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wuankang
 * @Date 2024/10/8 16:56
 * @Description TODO 创建延迟和立即订单对象
 * @Version 1.0
 */
@Data
public class CreateOrderRequest {
    /**
     * 商品
     */
    @NotBlank(message = "商品不能为空")
    private String goods;
    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    /**
     * 延迟时间（单位：秒）
     */
    private long delaySecond;
}
