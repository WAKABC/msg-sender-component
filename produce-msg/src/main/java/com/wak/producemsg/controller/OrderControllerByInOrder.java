package com.wak.producemsg.controller;

import cn.hutool.core.util.IdUtil;
import com.wak.msgspringbootstarter.sender.IMsgSender;
import com.wak.producemsg.dto.SequentialOrderMsg;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


/**
 * @author wuankang
 * @Date 2024/10/31 16:30:19
 * @Description TODO 发送顺序消息
 * @Version 1.0.0
 */
@RestController
public class OrderControllerByInOrder {

    @Resource
    private IMsgSender msgSender;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 发送顺序消息
     *
     * @return {@link ResponseEntity }<{@link String }>
     */
    @PostMapping("/sendSequential")
    public ResponseEntity<String> sendSequential() {
        String orderId = IdUtil.fastSimpleUUID();
        List<String> list = Arrays.asList("订单创建消息",
                "订单支付消息",
                "订单已发货",
                "买家确认收货",
                "订单已完成");
        for (String type : list) {
            SequentialOrderMsg sequentialOrderMsg = new SequentialOrderMsg();
            sequentialOrderMsg.setOrderId(orderId);
            sequentialOrderMsg.setType(type);
            msgSender.convertAndSequentialSend(orderId,
                    exchange,
                    routingKey,
                    applicationName,
                    IdUtil.fastSimpleUUID(),
                    sequentialOrderMsg);

        }
        return ResponseEntity.ok("success");
    }
}
