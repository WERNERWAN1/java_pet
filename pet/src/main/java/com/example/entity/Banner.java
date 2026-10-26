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
@TableName("b_banner")
public class Banner extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    @TableField
    public String image;
    @TableField(exist = false)
    public MultipartFile imageFile;
    @TableField
    public Long thingId;
}
