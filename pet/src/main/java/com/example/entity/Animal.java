package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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
    private String healthCondition;
    private String introduction;

    @TableField(exist = false)
    private MultipartFile imageFile;
}