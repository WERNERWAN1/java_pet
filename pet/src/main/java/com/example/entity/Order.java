package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("b_order")
public class Order extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    @TableField
    public String status;
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date orderTime;
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date payTime;
    @TableField
    public Long thingId;
    /**
     * 成交单价快照：下单瞬间从商品表固化到订单，避免商品后续调价导致历史订单金额失真。
     * 使用 BigDecimal 定点数，规避 float/double 的二进制精度误差。
     */
    @TableField
    public BigDecimal unitPrice;
    /**
     * 成交金额快照：unitPrice × count，下单时固化，作为订单金额的唯一权威来源。
     */
    @TableField
    public BigDecimal amount;
    @TableField
    public String orderType; // 订单类型(thing-宠物商品,goods-宠物商城用品)
    @TableField
    public Long userId;
    @TableField
    public Integer count;
    @TableField
    public String orderNumber; // 订单编号
    @TableField
    public String receiverAddress;
    @TableField
    public String receiverName;
    @TableField
    public String receiverPhone;
    @TableField
    public String remark;

    @TableField(exist = false)
    public String username; // 用户名
    @TableField(exist = false)
    public String title; // 商品名称
    @TableField(exist = false)
    public String cover; // 商品封面
    /**
     * 商品现价（非订单字段，仅用于列表展示对比）。
     * 金额统一使用 BigDecimal，与数据库 decimal(10,2) 对应，不再用 String 传递。
     */
    @TableField(exist = false)
    public BigDecimal price;
}
