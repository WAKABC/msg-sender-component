package com.wak.producemsg.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wuankang
 * @Date 2024/10/31 16:32:51
 * @Description TODO 订单表
 * @Version 1.0.0
 */
@Data
@TableName(value = "t_order")
public class OrderPO implements Serializable {
    /**
     * 订单id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 商品
     */
    @TableField(value = "goods")
    private String goods;

    /**
     * 订单金额
     */
    @TableField(value = "price")
    private BigDecimal price;

    private static final long serialVersionUID = 1L;
}