package com.wak.consumemsg.entities;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wuankang
 * @Date 2024/10/31 17:01:36
 * @Description TODO 消息消费日志
 * @Version 1.0.0
 */
@Data
@TableName(value = "t_msg_consume_log")
public class MsgConsumeLog implements Serializable {
    /**
     * 消息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 消息和消费者关联记录
     */
    @TableField(value = "msg_consume_id")
    private String msgConsumeId;

    /**
     * 消费状态，1：消费成功，2：消费失败
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

    @Serial
    private static final long serialVersionUID = 1L;
}