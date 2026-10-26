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
@TableName("b_comment")
public class Comment extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    @TableField
    public String content;
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date commentTime;
    @TableField
    public Integer likeCount;
    @TableField
    public Long userId;
    @TableField(exist = false)
    public String username; // 用户名
    @TableField
    public Long thingId;
    @TableField(exist = false)
    public String title; // 商品名称
    @TableField(exist = false)
    public String cover; // 商品封面
}
