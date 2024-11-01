package com.wak.consumemsg.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author wuankang
 * @Date 2024/10/31 17:01:09
 * @Description TODO 幂等辅助表
 * @Version 1.0.0
 */
@Data
@TableName(value = "t_idempotent")
public class Idempotent implements Serializable {
    /**
     * id，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 需要确保幂等的key
     */
    @TableField(value = "idempotent_key")
    private String idempotentKey;

    @Serial
    private static final long serialVersionUID = 1L;
}