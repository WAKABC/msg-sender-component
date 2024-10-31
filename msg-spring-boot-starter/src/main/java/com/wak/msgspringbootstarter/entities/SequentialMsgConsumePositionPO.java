package com.wak.msgspringbootstarter.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

/**
 * @author wuankang
 * @version 1.0.0
 * @date 2024/10/22
 * @description sequential消息consume position对象
 */
@Data
@TableName("t_sequential_msg_consume_position")
public class SequentialMsgConsumePositionPO {

    /**
     * id 主键
     */
    private String id;

    /**
     * 消息组id
     */
    private String groupId;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 消费位置
     */
    private Long consumeNumbering;

    /**
     * 版本号，每次更新+1
     */
    @Version
    private Long version;
}
