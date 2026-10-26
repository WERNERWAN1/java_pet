package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("room")
public class Room extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField
    private String name;
    @TableField
    private String status;
    @TableField
    private String animal;
}
