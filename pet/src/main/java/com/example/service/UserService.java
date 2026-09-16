package com.example.service;


import com.example.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUserList(String keyword);
    User getAdminUser(User user);
    User getNormalUser(User user);
    void createUser(User user);
    void deleteUser(String id);

    void updateUser(User user);

    User getUserByToken(String token);
    User getUserByUserName(String username);

    User getUserDetail(String userId);
    
    // 登录验证
    User loginAdminUser(String username, String password);
    User loginUser(String username, String password);
    
    // 用户注册
    boolean registerUser(User user);
    
    // 修改密码
    boolean updatePassword(String userId, String oldPassword, String newPassword);
    
    // 更新用户信息
    boolean updateUserInfo(User user);
}
