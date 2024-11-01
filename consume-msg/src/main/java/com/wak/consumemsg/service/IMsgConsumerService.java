package com.wak.consumemsg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wak.consumemsg.entities.MsgConsume;
import com.wak.msgspringbootstarter.dto.MessageEnvelope;

import java.time.LocalDateTime;

/**
 * @author wuankang
 * @Date 2024/10/31 17:06:32
 * @Description TODO
 * @Version 1.0
 */
public interface IMsgConsumerService extends IService<MsgConsume> {
    /**
     * 获取并创建
     *
     * @return {@code MsgConsume }
     */
    MsgConsume getAndCreate(MessageEnvelope<?> messageEnvelope, String consumerClassName, String queue);

    /**
     * 更新状态成功
     *
     * @param id             消息ID
     * @param consumerStatus 消费者状态
     */
    void updateStatusSuccess(String id, int consumerStatus);

    /**
     * 更新状态失败
     *
     * @param id             消息ID
     * @param consumerStatus 消费者状态
     * @param failMsg        失败消息
     * @param consumerRetry  消费者重试
     * @param nextRetryTime  下次重试时间
     */
    void updateStatusFail(String id, int consumerStatus, String failMsg, int consumerRetry, LocalDateTime nextRetryTime);
}
