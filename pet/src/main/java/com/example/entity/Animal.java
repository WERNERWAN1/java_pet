package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("animal")
public class Animal {
    @TableId(value = "pid", type = IdType.AUTO)
    private Integer pid;
    private String photo;
    private String nickname;
    private String gender;
    private Integer years;
    private String breed;
    private String condition;
    private String introduction;
}