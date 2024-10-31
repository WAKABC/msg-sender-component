package com.wak.msgspringbootstarter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wak.msgspringbootstarter.entities.MsgPO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wuankang
 * @date 2024/10/8 19:23
 * @Description 消息接口
 * @Version 1.0
 */
public interface IMsgService extends IService<MsgPO>{
    /**
     * 批量插入
     *
     * @param exchange        rabbit交换机
     * @param routingKey      rabbit路由键
     * @param expectSendTime  预计发送时间
     * @param msgBodyJsonList 消息身体json列表
     * @return {@code List<MsgPO> }
     */
    List<MsgPO> batchInsert(String exchange, String routingKey, LocalDateTime expectSendTime, List<String> msgBodyJsonList);

    /**
     * 更新消息状态成功
     *
     * @param msgPO 消息对象
     */
    void updateMsgStatusSuccess(MsgPO msgPO);

    /**
     * 更新消息状态失败
     *
     * @param msgPO             消息对象
     * @param sendRetry         发送重试
     * @param nextSendRetryTime 下次发送重试时间
     * @param failMsg           失败消息
     */
    void updateMsgStatusFailure(MsgPO msgPO, int sendRetry, LocalDateTime nextSendRetryTime, String failMsg);

}
