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
@TableName("user")
public class User extends BaseEntity {
    public static final int NormalUser = 1;
    public static final int DemoUser = 2;
    public static final int AdminUser = 3;

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    @TableField
    public String username;
    @TableField
    public String password;
    @TableField(exist = false)
    public String rePassword;
    @TableField
    public String nickname;
    @TableField
    public String mobile;
    @TableField
    public String email;
    @TableField
    public String description;
    @TableField
    public String role;
    @TableField
    public String status;
    @TableField
    public Integer score;
    @TableField
    public String avatar;
    @TableField(exist = false)
    public MultipartFile avatarFile;
    @TableField
    public String token;
    @TableField
    public String pushEmail;
    @TableField
    public String pushSwitch;
}
