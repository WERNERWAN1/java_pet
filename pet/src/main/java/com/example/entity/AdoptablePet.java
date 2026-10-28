package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("adoptable_pet")
public class AdoptablePet extends BaseEntity {
    @TableId(value = "pid", type = IdType.AUTO)
    private Long pid;
    @TableField
    private String photo;
    @TableField
    private String nickname;
    @TableField
    private String gender;
    @TableField
    private BigDecimal years;
    @TableField
    private String breed;
    @TableField
    private String healthCondition;
    @TableField
    private String introduction;

    @TableField(exist = false)
    private MultipartFile imageFile;
}
