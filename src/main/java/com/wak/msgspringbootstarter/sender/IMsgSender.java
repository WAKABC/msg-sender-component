package com.wak.msgspringbootstarter.sender;

import com.wak.msgspringbootstarter.dto.MessageEnvelope;
import com.wak.msgspringbootstarter.entities.MsgPO;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author wuankang
 * @date 2024/10/9 14:59
 * @Description 消息发送接口，所有外接系统统一调用send发送消息
 * @Version 1.0
 */
public interface IMsgSender {
    /**
     * 立即发送批次
     *
     * @param msgList 消息列表
     */
    void send(String exchange, String routingKey, List<MessageEnvelope<?>> msgList);

    /**
     * 立即发送单例
     *
     * @param msg 消息
     */
    default void send(String exchange, String routingKey, MessageEnvelope<?> msg) {
        Objects.requireNonNull(msg);
        this.send(exchange, routingKey, Collections.singletonList(msg));
    }

    /**
     * 延迟批量发送
     *
     * @param delayTime 延迟时间
     * @param timeUnit  时间单位
     * @param msgList   消息列表
     */
    void send(String exchange, String routingKey, long delayTime, TimeUnit timeUnit, List<MessageEnvelope<?>> msgList);

    /**
     * 延迟单条发送
     *
     * @param delayTime 延迟时间
     * @param timeUnit  时间单位
     * @param msg       消息
     */
    default void send(String exchange, String routingKey, long delayTime, TimeUnit timeUnit, MessageEnvelope<?> msg) {
        this.send(exchange, routingKey, delayTime, timeUnit, Collections.singletonList(msg));
    }


    /**
     * 多条消息即刻转换并发送
     *
     * @param exchange      交换
     * @param routingKey    路由键
     * @param producerName  生产者名
     * @param producerBusId 生产者业务id
     * @param msgList       消息列表
     */
    default void convertAndSend(String exchange, String routingKey, String producerName, String producerBusId, List<?> msgList) {
        this.send(exchange, routingKey, MessageEnvelope.convert(producerName, producerBusId, msgList));
    }

    /**
     * 单条消息即刻转换并发送
     *
     * @param exchange      交换
     * @param routingKey    路由键
     * @param producerName  生产者名称
     * @param producerBusId 生产者业务 ID
     * @param msg           消息
     */
    default void convertAndSend(String exchange, String routingKey, String producerName, String producerBusId, Object msg) {
        this.convertAndSend(exchange, routingKey, producerName, producerBusId, Collections.singletonList(msg));
    }

    /**
     * 多条消息延迟转换并发送
     *
     * @param exchange      交换
     * @param routingKey    路由键
     * @param delayTime     延迟时间
     * @param timeUnit      时间单位
     * @param producerName  生产者名称
     * @param producerBusId 生产者业务 ID
     * @param msgList       消息列表
     */
    default void convertAndSend(String exchange, String routingKey, long delayTime, TimeUnit timeUnit, String producerName, String producerBusId, List<?> msgList) {
        this.send(exchange, routingKey, delayTime, timeUnit, MessageEnvelope.convert(producerName, producerBusId, msgList));
    }

    /**
     * 单条消息延迟转换并发送
     *
     * @param exchange      交换
     * @param routingKey    路由键
     * @param delayTime     延迟时间
     * @param timeUnit      时间单位
     * @param producerName  生产者名称
     * @param producerBusId 生产者业务ID
     * @param msg           消息
     */
    default void convertAndSend(String exchange, String routingKey, long delayTime, TimeUnit timeUnit, String producerName, String producerBusId, Object msg) {
        this.convertAndSend(exchange, routingKey, delayTime, timeUnit, producerName, producerBusId, Collections.singletonList(msg));
    }

    /**
     * 发送顺序消息
     *
     * @param busId      业务ID
     * @param exchange   交换
     * @param routingKey 路由键
     * @param msg    消息
     */
    void sendSequentialMsg(String busId, String exchange, String routingKey, MessageEnvelope<?> msg);

    /**
     * 转换并顺序发送
     *
     * @param busId         业务ID
     * @param exchange      交换
     * @param routingKey    路由键
     * @param producerName  生产者名称
     * @param producerBusId 制片人业务ID
     * @param msg           消息
     */
    default void convertAndSequentialSend(String busId, String exchange, String routingKey, String producerName, String producerBusId, Object msg) {
        this.sendSequentialMsg(busId, exchange, routingKey, new MessageEnvelope<>(producerBusId, producerName, msg));
    }

    /**
     * 消息重发
     *
     * @param msgPO 消息对象
     */
    void sendRetry(MsgPO msgPO);
}
