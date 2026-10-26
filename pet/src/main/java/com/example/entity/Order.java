package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @TableField(exist = false)
    public String price; // 商品价格
}
