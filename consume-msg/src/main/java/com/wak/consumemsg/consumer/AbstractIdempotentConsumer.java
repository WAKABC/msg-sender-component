package com.wak.consumemsg.consumer;

import com.wak.consumemsg.entities.MsgConsume;
import com.wak.consumemsg.service.IdempotentService;
import com.wak.msgspringbootstarter.dto.MessageEnvelope;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

/**
 * @author wuankang
 * @Date 2024/10/23 14:26
 * @Description TODO 普通（延迟和立即）消费服务具备幂等功能
 * @Version 1.0
 */
@Slf4j
public abstract class AbstractIdempotentConsumer<B, M extends MessageEnvelope<B>> extends AbstractConsumerService<B, M> {
    @Resource
    private IdempotentService idempotentService;

    @Override
    public void dispose(Message message) {
        super.dispose(message);
    }

    /**
     * 具体消费逻辑，增加幂等判断
     *
     * @param message    消息
     * @param msg        消息
     * @param msgConsume 消息消费
     */
    @Override
    protected void consume(Message message, M msg, MsgConsume msgConsume) {
        String idempotentKey = getIdempotentKey(msgConsume);
        //调用幂等工具类确保消息只会被成功消费一次，这里可以确保同样的idempotentKey，即使发生并发，下面方法第二个参数中的逻辑只会被成功处理一次
        int result = this.idempotentService.idempotent(idempotentKey, () -> {
            //调用子类的方法，消费消息
            this.idempotentConsume(message, msg, msgConsume);
        });
        if (result == -1) {
            log.info("重复消息，无需消费");
        }
    }


    /**
     * 幂等key
     *
     * @param msgConsumer 消息消费对象
     * @return string
     */
    private String getIdempotentKey(MsgConsume msgConsumer) {
        return String.format("producer:%s,producerBusId:%s,consumerClassName:%s", msgConsumer.getProducer(), msgConsumer.getProducerBusId(), msgConsumer.getConsumerClassName());
    }

    /**
     * 需要幂等处理的逻辑，由子类实现
     *
     * @param message     消息
     * @param msg         消息
     * @param msgConsumer 消息消费者
     */
    protected abstract void idempotentConsume(Message message, MessageEnvelope<B> msg, MsgConsume msgConsumer);
}
