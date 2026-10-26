package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("b_notice")
public class Notice extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    @TableField
    public String title;
    @TableField
    public String content;
}
