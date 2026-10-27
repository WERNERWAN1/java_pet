package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("thing")
public class Thing extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    @TableField
    public String title;
    @TableField
    public String nickname;
    @TableField
    public String sex;
    @TableField
    public String address;
    @TableField
    public String cover;
    @TableField
    public String description;
    @TableField
    public BigDecimal price;
    @TableField
    public String status;
    @TableField
    public Integer score;
    @TableField
    public Integer pv;
    @TableField
    public Integer recommendCount;
    @TableField
    public Integer wishCount;
    @TableField
    public Integer collectCount;
    @TableField
    public Integer stock;
    @TableField
    public Long classificationId;

    @TableField(exist = false)
    public List<Long> tags; // 标签

    @TableField(exist = false)
    public MultipartFile imageFile;
}
