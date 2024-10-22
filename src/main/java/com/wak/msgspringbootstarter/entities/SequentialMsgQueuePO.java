package com.wak.msgspringbootstarter.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wuankang
 * @version 1.0.0
 * @date 2024/10/22
 * @description sequential消息queue对象
 */
@Data
@TableName("t_sequential_msg_queue")
public class SequentialMsgQueuePO {

    /**
     * id 主键
     */
    private String id;

    /**
     * 消息组id
     */
    private String groupId;

    /**
     * 消费位置
     */
    private Long numbering;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 消息json格式
     */
    private String msgJson;
}
