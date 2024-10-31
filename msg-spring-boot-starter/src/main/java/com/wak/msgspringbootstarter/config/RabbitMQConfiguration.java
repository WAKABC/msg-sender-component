package com.wak.msgspringbootstarter.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuankang
 * @date 2024/10/16 15:56
 * @Description TODO
 * @Version 1.0
 */
@Configuration
@ConditionalOnProperty(value = "spring.rabbitmq.template.exchange")
public class RabbitMQConfiguration {
    @Value("${spring.rabbitmq.template.exchange}")
    public final String EXCHANGE = "orderExchange";
    @Value("${spring.rabbitmq.template.default-receive-queue}")
    public final String QUEUE = "orderQueue";
    @Value("${spring.rabbitmq.template.routing-key}")
    public final String ROUTING_KEY = "create";

    @Bean
    public DirectExchange orderExchange() {
        //不存在就自动创建交换机， durable 为 true，autoDelete 为 false。
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding orderExchangeBindOrderQueue() {
        return new Binding(QUEUE, Binding.DestinationType.QUEUE, EXCHANGE, ROUTING_KEY, null);
    }
}
