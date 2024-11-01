package com.wak.consumemsg.consumer;

import cn.hutool.json.JSONUtil;
import com.wak.consumemsg.entities.SequentialOrderMsg;
import com.wak.msgspringbootstarter.dto.MessageEnvelope;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author wuankang
 * @Date 2024/10/31 16:50:33
 * @Description 顺序消费实现类
 * @Version 1.0
 */
@Component
public class OrderConsumerByInOrder extends AbstractSequentialMsgConsumer<SequentialOrderMsg, MessageEnvelope<SequentialOrderMsg>> {

    /**
     * 消费顺序消息，监听default-receive-queue队列，使用5个线程来消费
     * @param message mq消息
     */
    @Override
    @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}", concurrency = "5")
    public void dispose(Message message) {
        super.dispose(message);
    }

    /**
     * 顺序消费顺序消息
     *
     * @param msg 消息
     */
    @Override
    protected void sequentialMsgConsume(MessageEnvelope<SequentialOrderMsg> msg) {
        String log = "☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆顺序消息消费,消息编号：" + msg.getSequentialMsgNumbering() + ",消息体：" + JSONUtil.toJsonStr(msg.getBody());
        System.err.println(log);
        //这里休眠500ms，模拟耗时的业务操作
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
