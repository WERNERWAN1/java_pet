package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("adopt")
public class Adopt {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;
    private Integer ownerUid;
    private Integer petUid;
    private String adoptDate;
    private String state;
}