package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.service.UserService;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SALT = "abcd1234";

    @Autowired
    UserMapper userMapper;

    /**
     * 对密码进行MD5加密
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex((password + SALT).getBytes());
    }

    /**
     * 生成用户token
     */
    private String generateToken(String username) {
        return DigestUtils.md5DigestAsHex((username + SALT).getBytes());
    }

    @Override
    public List<User> getUserList(String keyword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        if(StringUtils.isNotBlank(keyword)){
            // like查询
            queryWrapper.like("username", keyword);
        }
        queryWrapper.orderBy(true, false, "create_time");
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public User getAdminUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.gt("role", "1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getNormalUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.eq("role", "1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void createUser(User user) {
        user.setPassword(encryptPassword(user.getPassword()));
        user.setToken(generateToken(user.getUsername()));
        userMapper.insert(user);
    }

    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(id);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    @Override
    public User getUserByToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("token", token);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserByUserName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserDetail(String userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User loginAdminUser(String username, String password) {
        String encryptedPassword = encryptPassword(password);
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", encryptedPassword);
        queryWrapper.gt("role", "1");
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User loginUser(String username, String password) {
        String encryptedPassword = encryptPassword(password);
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", encryptedPassword);
        queryWrapper.eq("role", "1");
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean registerUser(User user) {
        // 检查用户名是否已存在
        if (getUserByUserName(user.getUsername()) != null) {
            return false;
        }
        
        // 加密密码并设置token
        user.setPassword(encryptPassword(user.getPassword()));
        user.setToken(generateToken(user.getUsername()));
        
        // 设置默认值
        user.setRole(String.valueOf(User.NormalUser));
        user.setStatus("0");
        user.setCreateTime(String.valueOf(System.currentTimeMillis()));
        
        // 保存用户
        userMapper.insert(user);
        return true;
    }
    
    @Override
    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        User user = getUserDetail(userId);
        if (user == null) {
            return false;
        }
        
        // 验证旧密码
        String encryptedOldPassword = encryptPassword(oldPassword);
        if (!user.getPassword().equals(encryptedOldPassword)) {
            return false;
        }
        
        // 更新密码
        String encryptedNewPassword = encryptPassword(newPassword);
        user.setPassword(encryptedNewPassword);
        userMapper.updateById(user);
        return true;
    }
    
    @Override
    public boolean updateUserInfo(User user) {
        // 不能修改用户名和密码
        user.setUsername(null);
        user.setPassword(null);
        
        userMapper.updateById(user);
        return true;
    }
}
