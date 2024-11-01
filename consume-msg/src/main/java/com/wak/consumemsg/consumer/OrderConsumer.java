package com.wak.consumemsg.consumer;

import com.wak.consumemsg.entities.MsgConsume;
import com.wak.consumemsg.entities.OrderPO;
import com.wak.msgspringbootstarter.dto.MessageEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wuankang
 * @Date 2024/10/31 16:50:24
 * @Description TODO 延迟和立即订单消费者带有幂等处理
 * @Version 1.0
 */
@Slf4j
@Component
public class OrderConsumer extends AbstractIdempotentConsumer<OrderPO, MessageEnvelope<OrderPO>> {
    private final AtomicInteger atomicInteger = new AtomicInteger();

    /**
     * 监听消息
     *
     * @param message 信息
     */
//    @RabbitListener(queues = {"${spring.rabbitmq.template.default-receive-queue}"})
    @Override
    public void dispose(Message message) {
        super.dispose(message);
    }

    @Override
    protected void idempotentConsume(Message message, MessageEnvelope<OrderPO> msg, MsgConsume msgConsumer) {
        atomicInteger.getAndIncrement();
        //故意使坏使坏，金额为负为零时故意出错抛出异常
        if (msg.getBody().getPrice().signum() <= 0) {
            throw new RuntimeException("订单金额为负为零时error");
        }
    }
}
