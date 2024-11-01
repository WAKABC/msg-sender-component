package com.wak.consumemsg.entities;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wuankang
 * @Date 2024/10/31 17:01:27
 * @Description TODO 消息和消费者关联表
 * @Version 1.0.0
 */
@Data
@TableName(value = "t_msg_consume")
public class MsgConsume implements Serializable {
    /**
     * 消息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 生产者名称
     */
    @TableField(value = "producer")
    private String producer;

    /**
     * 生产者这边消息的唯一标识
     */
    @TableField(value = "producer_bus_id")
    private String producerBusId;

    /**
     * 消费者完整类名
     */
    @TableField(value = "consumer_class_name")
    private String consumerClassName;

    /**
     * 队列名称
     */
    @TableField(value = "queue_name")
    private String queueName;

    /**
     * 消息体,json格式
     */
    @TableField(value = "body_json")
    private String bodyJson;

    /**
     * 消息状态，0：待消费，1：消费成功，2：消费失败
     */
    @TableField(value = "`STATUS`")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * status=2 时，记录消息消费失败的原因
     */
    @TableField(value = "fail_msg")
    private String failMsg;

    /**
     * 已投递失败次数
     */
    @TableField(value = "fail_count")
    private Integer failCount;

    /**
     * 消费失败后，是否还需要重试？1：是，0：否
     */
    @TableField(value = "consume_retry")
    private Integer consumeRetry;

    /**
     * 投递失败后，下次重试时间
     */
    @TableField(value = "next_retry_time")
    private LocalDateTime nextRetryTime;

    /**
     * 最近更新时间
     */
    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}