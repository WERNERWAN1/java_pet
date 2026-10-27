package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("adopt")
public class Adopt extends BaseEntity {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;
    @TableField
    private Long ownerUid;
    @TableField
    private Long petUid;
    @TableField
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date adoptDate;
    @TableField
    private String state;
}
