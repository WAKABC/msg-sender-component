package com.wak.msgspringbootstarter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wak.msgspringbootstarter.entities.SequentialMsgQueuePO;

/**
 * 顺序消息排队表
 */

public interface ISequentialMsgQueueService extends IService<SequentialMsgQueuePO> {
    /**
     * 压入一条顺序消息
     *
     * @param queueName 消费者队列名称
     * @param groupId   组id
     * @param numbering 消息编号
     * @param msgJson   顺序消息json格式
     * @return 记录id
     */
    SequentialMsgQueuePO push(String groupId, Long numbering, String queueName, String msgJson);

    /**
     * 获取队列头部记录
     *
     * @param groupId   组id
     * @param queueName 消费者队列
     * @return
     */
    SequentialMsgQueuePO getFirst(String groupId, String queueName);

    /**
     * 删除记录
     *
     * @param id
     */
    void delete(String id);

}
