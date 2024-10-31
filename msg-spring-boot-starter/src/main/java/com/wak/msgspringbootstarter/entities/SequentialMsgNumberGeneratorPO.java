package com.wak.msgspringbootstarter.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

/**
 * @author wuankang
 * @version 1.0.0
 * @date 2024/10/22
 * @description sequential消息number generator对象
 */
@Data
@TableName("t_sequential_msg_number_generator")
public class SequentialMsgNumberGeneratorPO {

    /**
     * id 主键
     */
    private String id;

    /**
     * 消息组id
     */
    private String groupId;

    /**
     * 消息当前编号
     */
    private Long numbering;

    @Version
    private Long version;
}
