package com.wak.producemsg.controller;

import com.wak.producemsg.dto.CreateOrderRequest;
import com.wak.producemsg.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuankang
 * @Date 2024/10/31 16:30:53
 * @Description TODO 发送延迟和立即订单
 * @Version 1.0.0
 */
@RestController
@Slf4j
public class OrderController {

    @Resource
    private IOrderService orderService;

    /**
     * 演示：创建订单，模拟投递消息
     *
     * @param req 请求
     * @return {@link ResponseEntity }<{@link String }>
     */
    @PostMapping("/order/createOrder")
    public ResponseEntity<String> createOrder(@Validated @RequestBody CreateOrderRequest req) {
        log.info("创建订单接口....");
        String order = this.orderService.createOrder(req);
        return ResponseEntity.ok(order);
    }
}