package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

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
    /** 寄养宠物照片（存文件名） */
    @TableField
    private String animalPhoto;

    /** 前端上传的文件（不映射数据库） */
    @TableField(exist = false)
    private MultipartFile imageFile;
}
