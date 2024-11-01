package com.wak.producemsg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wak.producemsg.dto.CreateOrderRequest;
import com.wak.producemsg.entities.OrderPO;

/**
 * @author wuankang
 * @Date 2024/10/31 16:34:43
 * @Description 订单接口
 * @Version 1.0
 */
public interface IOrderService extends IService<OrderPO> {
    /**
     * 创建订单
     *
     * @param request 要求
     * @return {@code String }
     */
    String createOrder(CreateOrderRequest request);
}
