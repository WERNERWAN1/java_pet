package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("room")
public class Room {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String status;
    private String animal;
}