package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {
    public static final int NormalUser = 1;
    public static final int DemoUser = 2;
    public static final int AdminUser = 3;

    /** 账户状态：正常 */
    public static final int StatusNormal = 0;
    /** 账户状态：封号 */
    public static final int StatusBanned = 1;

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
    public String gender;
    @TableField
    public String description;
    @TableField
    public Integer role;
    @TableField
    public Integer status;
    @TableField
    public Integer score;
    @TableField
    public String avatar;
    @TableField(exist = false)
    public MultipartFile avatarFile;
    @TableField
    public String token;
    /** 登录令牌过期时间，不返回给前端 */
    @TableField
    @JsonIgnore
    public Date tokenExpireTime;
    @TableField
    public String pushEmail;
    @TableField
    public Integer pushSwitch;
}
