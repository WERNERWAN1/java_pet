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
@TableName("b_op_log")
public class OpLog extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    @TableField
    public String reIp;
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date reTime;
    @TableField
    public String reUa;
    @TableField
    public String reUrl;
    @TableField
    public String reMethod;
    @TableField
    public String reContent;
    @TableField
    public Integer accessTime; // 访问耗时(毫秒)
}
