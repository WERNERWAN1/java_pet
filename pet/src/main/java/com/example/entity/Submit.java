package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("submit")
public class Submit {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String img;
    private String time;
    private String status;
}