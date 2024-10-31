package com.wak.msgspringbootstarter.entities;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wuankang
 * @date 2024/10/8 19:44
 * @Description TODO
 * @Version 1.0
 */

/**
 * 本地消息表
 */
@Data
@TableName(value = "t_msg")
public class MsgPO implements Serializable {
    /**
     * 消息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 交换机
     */
    @TableField(value = "exchange")
    private String exchange;

    /**
     * 路由key
     */
    @TableField(value = "routing_key")
    private String routingKey;

    /**
     * 消息体,json格式
     */
    @TableField(value = "body_json")
    private String bodyJson;

    /**
     * 消息状态，0：待投递到mq，1：投递成功，2：投递失败
     */
    @TableField(value = "`STATUS`")
    private Integer status;

    /**
     * 消息期望投递时间，大于当前时间，则为延迟消息，否则会立即投递
     */
    @TableField(value = "expect_send_time")
    private LocalDateTime expectSendTime;

    /**
     * 消息实际投递时间
     */
    @TableField(value = "actual_send_time")
    private LocalDateTime actualSendTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * status=2 时，记录消息投递失败的原因
     */
    @TableField(value = "fail_msg")
    private String failMsg = null;

    /**
     * 已投递失败次数
     */
    @TableField(value = "fail_count", update = "fail_count + 1")
    private Integer failCount = 0;

    /**
     * 投递MQ失败了，是否还需要重试？1：是，0：否
     */
    @TableField(value = "send_retry")
    private Integer sendRetry = 0;

    /**
     * 投递失败后，下次重试时间
     */
    @TableField(value = "next_retry_time")
    private LocalDateTime nextRetryTime;

    /**
     * 最近更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}