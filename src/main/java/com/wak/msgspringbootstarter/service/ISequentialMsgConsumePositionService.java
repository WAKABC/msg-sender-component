package com.wak.msgspringbootstarter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wak.msgspringbootstarter.entities.SequentialMsgConsumePositionPO;

/**
 * @author wuankang
 * @date 2024/10/22 15:57
 * @Description 顺序消息消费信息表
 * @Version 1.0
 */
public interface ISequentialMsgConsumePositionService extends IService<SequentialMsgConsumePositionPO> {
    /**
     * 根据 groupId  & queueName 获取记录，不存在的时候则创建
     *
     * @param groupId 组ID
     * @param queueName 队列名称
     * @return SequentialMsgConsumePositionPO
     */
    SequentialMsgConsumePositionPO getAndCreate(String groupId, String queueName);
}
