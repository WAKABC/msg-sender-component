package com.wak.consumemsg.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wak.consumemsg.entities.MsgConsume;
import com.wak.consumemsg.enums.MsgConsumerStatusEnum;
import com.wak.consumemsg.mapper.MsgConsumeMapper;
import com.wak.consumemsg.service.IMsgConsumerService;
import com.wak.msgspringbootstarter.dto.MessageEnvelope;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author wuankang
 * @Date 2024/10/31 17:06:41
 * @Description TODO
 * @Version 1.0
 */
@Service
public class MsgConsumerServiceImpl extends ServiceImpl<MsgConsumeMapper, MsgConsume> implements IMsgConsumerService {
    @Resource
    private MsgConsumeMapper msgConsumeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MsgConsume getAndCreate(MessageEnvelope<?> messageEnvelope, String consumerClassName, String queue) {
        LambdaQueryWrapper<MsgConsume> eq = Wrappers.lambdaQuery(MsgConsume.class)
                .eq(MsgConsume::getProducer, messageEnvelope.getProducerName())
                .eq(MsgConsume::getProducerBusId, messageEnvelope.getProducerBusId())
                .eq(MsgConsume::getConsumerClassName, consumerClassName);
        MsgConsume msgConsume = this.getOne(eq);
        //新增记录
        if (Objects.isNull(msgConsume)) {
            msgConsume = this.insert(messageEnvelope, consumerClassName, queue);
        }
        return msgConsume;
    }

    /**
     * 插入记录
     *
     * @param messageEnvelope   邮件信封
     * @param consumerClassName Consumer 类名称
     * @param queueName         队列名称
     * @return {@link MsgConsume }
     */
    public MsgConsume insert(MessageEnvelope<?> messageEnvelope, String consumerClassName, String queueName) {
        String producerName = messageEnvelope.getProducerName();
        String producerBusId = messageEnvelope.getProducerBusId();
        String bodyJson = JSONUtil.toJsonStr(messageEnvelope.getBody());
        MsgConsume msgConsume = new MsgConsume();
        msgConsume.setId(IdUtil.fastSimpleUUID());
        msgConsume.setBodyJson(bodyJson);
        msgConsume.setProducer(producerName);
        msgConsume.setProducerBusId(producerBusId);
        msgConsume.setStatus(MsgConsumerStatusEnum.INIT.getStatus());
        msgConsume.setConsumerClassName(consumerClassName);
        msgConsume.setQueueName(queueName);
        msgConsume.setConsumeRetry(0);
        msgConsume.setFailCount(0);
        msgConsume.setCreateTime(LocalDateTime.now());
        this.save(msgConsume);
        return msgConsume;
    }

    /**
     * 更新状态成功
     *
     * @param id             消息ID
     * @param consumerStatus 消费者状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusSuccess(String id, int consumerStatus) {
        this.msgConsumeMapper.updateStatusSuccess(consumerStatus, LocalDateTime.now(), id);
    }

    /**
     * 更新状态失败
     *
     * @param id             消息ID
     * @param consumerStatus 消费者状态
     * @param failMsg        失败消息
     * @param consumerRetry  消费者重试
     * @param nextRetryTime  下次重试时间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusFail(String id, int consumerStatus, String failMsg, int consumerRetry, LocalDateTime nextRetryTime) {
        this.msgConsumeMapper.updateStatusFail(consumerStatus, failMsg, consumerRetry, nextRetryTime, id);
    }
}
