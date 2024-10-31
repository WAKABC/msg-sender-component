package com.wak.msgspringbootstarter.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuankang
 * @date 2024/10/15 0:41
 * @Description 统一消息类，用户传输消息
 * @Version 1.0
 */
@Data
public class MessageEnvelope<T> {
    /**
     * 生产者名称
     */
    private String producerName;
    /**
     * 生产者业务id
     */
    private String producerBusId;
    /**
     * 顺序消息所属组
     */
    private String sequentialMsgGroupId;

    /**
     * 顺序消息编号
     */
    private Long sequentialMsgNumbering;
    /**
     * 传输的消息体
     */
    private T body;

    public MessageEnvelope(String producerName, String producerBusId, T body) {
        this.producerName = producerName;
        this.producerBusId = producerBusId;
        this.body = body;
    }

    /**
     * 构建多条Message
     *
     * @param producerName  生产者名称
     * @param producerBusId 生产者业务id
     * @param bodyList      消息体
     * @return {@code Message<T> }
     */
    public static List<MessageEnvelope<?>> convert(@NonNull String producerName, @NonNull String producerBusId, @NonNull List<?> bodyList) {
        return bodyList.stream().map(body -> new MessageEnvelope<>(producerName, producerBusId, body)).collect(Collectors.toList());
    }
}
