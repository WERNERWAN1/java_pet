package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.User;
import com.example.permission.Access;
import com.example.permission.AccessLevel;
import com.example.service.BatchDeleteService;
import com.example.service.FileUploadService;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @author wanchun
 * @email email
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;
    
    @Autowired
    FileUploadService fileUploadService;
    
    @Autowired
    BatchDeleteService batchDeleteService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword){
        List<User> list =  userService.getUserList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(Long userId){
        User user =  userService.getUserDetail(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", user);
    }

    // 后台用户登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResponse login(User user){
        User responseUser = userService.loginAdminUser(user.getUsername(), user.getPassword());
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 退出登录：使当前令牌立即失效
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public APIResponse logout(String type, HttpServletRequest request) {
        if ("admin".equals(type)) {
            userService.logout(request.getHeader("ADMINTOKEN"));
        } else if ("user".equals(type)) {
            userService.logout(request.getHeader("TOKEN"));
        } else {
            userService.logout(request.getHeader("ADMINTOKEN"));
            userService.logout(request.getHeader("TOKEN"));
        }
        return new APIResponse(ResponeCode.SUCCESS, "退出成功");
    }

    // 普通用户登录
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin(User user){
        User responseUser = userService.loginUser(user.getUsername(), user.getPassword());
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户注册
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @Transactional
    public APIResponse userRegister(User user) throws IOException {
        if (!StringUtils.isEmpty(user.getUsername())
                && !StringUtils.isEmpty(user.getPassword())
                && !StringUtils.isEmpty(user.getRePassword())) {
            // 验证密码
            if(!user.getPassword().equals(user.getRePassword())) {
                return new APIResponse(ResponeCode.FAIL, "密码不一致");
            }
            
            // 处理头像上传
            String avatar = fileUploadService.uploadAvatar(user.getAvatarFile());
            if(!StringUtils.isEmpty(avatar)) {
                user.setAvatar(avatar);
            }
            
            // 注册用户（Service内部处理密码加密和token生成）
            boolean success = userService.registerUser(user);
            if (success) {
                return new APIResponse(ResponeCode.SUCCESS, "创建成功");
            } else {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(User user) throws IOException {
        if (!StringUtils.isEmpty(user.getUsername()) && !StringUtils.isEmpty(user.getPassword())) {
            // 处理头像上传
            String avatar = fileUploadService.uploadAvatar(user.getAvatarFile());
            if(!StringUtils.isEmpty(avatar)) {
                user.setAvatar(avatar);
            }
            
            // 创建用户（Service内部处理密码加密和token生成）
            boolean success = userService.registerUser(user);
            if (success) {
                return new APIResponse(ResponeCode.SUCCESS, "创建成功");
            } else {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        batchDeleteService.batchDelete(ids, id -> userService.deleteUser(Long.parseLong(id)));
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(User user) throws IOException {
        // update不能修改密码，故置空
        user.setPassword(null);
        // 处理头像上传
        String avatar = fileUploadService.uploadAvatar(user.getAvatarFile());
        if(!StringUtils.isEmpty(avatar)) {
            user.setAvatar(avatar);
        }
        userService.updateUser(user);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }


    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserInfo(User user) throws IOException {
        User tmpUser = userService.getUserDetail(user.getId());
        if(tmpUser.getRole() != null && tmpUser.getRole() == User.NormalUser){
            // 处理头像上传
            String avatar = fileUploadService.uploadAvatar(user.getAvatarFile());
            if(!StringUtils.isEmpty(avatar)) {
                user.setAvatar(avatar);
            }
            
            // 更新用户信息
            boolean success = userService.updateUserInfo(user);
            if (success) {
                return new APIResponse(ResponeCode.SUCCESS, "更新成功");
            } else {
                return new APIResponse(ResponeCode.FAIL, "更新失败");
            }
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @Transactional
    public APIResponse updatePwd(Long userId, String password, String newPassword) throws IOException {
        User user = userService.getUserDetail(userId);
        if(user.getRole() != null && user.getRole() == User.NormalUser) {
            // Service内部处理密码加密和验证
            boolean success = userService.updatePassword(userId, password, newPassword);
            if (success) {
                return new APIResponse(ResponeCode.SUCCESS, "更新成功");
            } else {
                return new APIResponse(ResponeCode.FAIL, "原密码错误");
            }
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }

}
