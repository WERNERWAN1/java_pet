package com.example.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
    /** 房间状态：1-空闲，2-已占用，3-维护中 */
    @TableField
    private Integer status;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String animal;
    /** 寄养宠物照片（存文件名） */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String animalPhoto;

    /** 前端上传的文件（不映射数据库） */
    @TableField(exist = false)
    private MultipartFile imageFile;
}
