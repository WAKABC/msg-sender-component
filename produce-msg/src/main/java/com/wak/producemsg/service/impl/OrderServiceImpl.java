package com.wak.producemsg.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wak.msgspringbootstarter.sender.IMsgSender;
import com.wak.producemsg.dto.CreateOrderRequest;
import com.wak.producemsg.entities.OrderPO;
import com.wak.producemsg.mapper.OrderMapper;
import com.wak.producemsg.service.IOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author wuankang
 * @Date 2024/10/31 16:35:22
 * @Description TODO
 * @Version 1.0
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderPO> implements IOrderService {
    /**
     * 消息发件器
     */
    @Resource
    private IMsgSender msgSender;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 创建订单
     *
     * @param request 要求
     * @return {@code String }
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(CreateOrderRequest request) {
        log.info("创建订单，request:{}", request);
        String goods = request.getGoods();
        BigDecimal price = request.getPrice();
        OrderPO orderPo = new OrderPO();
        orderPo.setId(IdUtil.simpleUUID());
        orderPo.setGoods(goods);
        orderPo.setPrice(price);
        //入库
        this.save(orderPo);
        //根据延迟时间进行分区投递
        long delaySecond = request.getDelaySecond();
        if (delaySecond > 0) {
            //延迟投递
            this.msgSender.convertAndSend(exchange, routingKey, delaySecond, TimeUnit.SECONDS, applicationName, IdUtil.fastSimpleUUID(), orderPo);
        } else {
            //立即投递
            this.msgSender.convertAndSend(exchange, routingKey, applicationName, IdUtil.fastSimpleUUID(), orderPo);
        }
        return orderPo.getId();
    }
}
