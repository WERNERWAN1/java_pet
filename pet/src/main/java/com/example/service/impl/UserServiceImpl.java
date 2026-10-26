package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.service.UserService;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 历史遗留的固定盐值。
     * 仅用于存量“加盐 MD5”密码的兼容校验与平滑迁移，新密码一律不再使用该盐。
     */
    @Deprecated
    private static final String LEGACY_SALT = "abcd1234";

    @Autowired
    UserMapper userMapper;

    /**
     * BCrypt 加密器，由 PasswordConfig 统一配置（成本因子 10）
     */
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * 对明文密码做 BCrypt 单向散列。
     * BCrypt 每次都会生成新的随机盐并内嵌在密文中，因此同一明文两次加密结果不同。
     */
    private String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 校验明文密码与库中密文是否匹配。
     * 优先按 BCrypt 校验；若命中历史遗留的加盐 MD5 数据，校验通过后会在登录流程中自动升级为 BCrypt。
     */
    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        if (isBcryptHash(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        // 兼容历史数据：加盐 MD5
        return legacyMd5(rawPassword).equals(storedPassword);
    }

    /**
     * 判断密文是否为 BCrypt 格式（$2a$ / $2b$ / $2y$ 前缀）
     */
    private boolean isBcryptHash(String storedPassword) {
        return storedPassword.startsWith("$2a$")
                || storedPassword.startsWith("$2b$")
                || storedPassword.startsWith("$2y$");
    }

    /**
     * 历史算法：固定盐 + MD5。仅用于存量数据兼容，不参与新密码的生成。
     */
    @Deprecated
    private String legacyMd5(String password) {
        return DigestUtils.md5DigestAsHex((password + LEGACY_SALT).getBytes());
    }

    /**
     * 生成用户 token。
     * 使用 UUID 随机数，避免旧实现中“token = MD5(用户名 + 固定盐)”可被任意推导、冒用他人身份的风险。
     */
    private String generateToken(String username) {
        return UUID.randomUUID().toString().replace("-", "");
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
        return loginByRole(user, true);
    }

    @Override
    public User getNormalUser(User user) {
        return loginByRole(user, false);
    }

    /**
     * 按角色登录校验：先按用户名取出用户，再对密码做单向散列比对，
     * 不再使用“where password = 密文”的等值查询（BCrypt 每次结果不同，等值查询必然失败）。
     */
    private User loginByRole(User user, boolean admin) {
        if (user == null) {
            return null;
        }
        User dbUser = getUserByUserName(user.getUsername());
        if (dbUser == null || !matchesPassword(user.getPassword(), dbUser.getPassword())) {
            return null;
        }
        int role = parseRole(dbUser.getRole());
        boolean matched = admin ? role > User.NormalUser : role == User.NormalUser;
        return matched ? clearSensitive(upgradePasswordIfNeeded(dbUser, user.getPassword())) : null;
    }

    private int parseRole(String role) {
        if (role == null) {
            return -1;
        }
        try {
            return Integer.parseInt(role);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public void createUser(User user) {
        user.setPassword(encryptPassword(user.getPassword()));
        user.setToken(generateToken(user.getUsername()));
        userMapper.insert(user);
    }

    @Override
    public void deleteUser(Long id) {
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
    public User getUserDetail(Long userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User loginAdminUser(String username, String password) {
        User user = getUserByUserName(username);
        if (user == null || !matchesPassword(password, user.getPassword())) {
            return null;
        }
        if (parseRole(user.getRole()) <= User.NormalUser) {
            return null;
        }
        return clearSensitive(upgradePasswordIfNeeded(user, password));
    }
    
    @Override
    public User loginUser(String username, String password) {
        User user = getUserByUserName(username);
        if (user == null || !matchesPassword(password, user.getPassword())) {
            return null;
        }
        if (parseRole(user.getRole()) != User.NormalUser) {
            return null;
        }
        return clearSensitive(upgradePasswordIfNeeded(user, password));
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
        user.setCreateTime(new Date());
        
        // 保存用户
        userMapper.insert(user);
        return true;
    }
    
    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserDetail(userId);
        if (user == null) {
            return false;
        }
        
        // 验证旧密码（兼容存量 MD5 数据）
        if (!matchesPassword(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新为新算法密文
        user.setPassword(encryptPassword(newPassword));
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

    /**
     * 登录成功后抹除敏感字段，避免密码密文随用户信息一同返回前端
     */
    private User clearSensitive(User user) {
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    /**
     * 存量密码平滑升级：登录或修改密码校验通过后，若库中仍是历史 MD5 密文，
     * 则用本次明文重新生成 BCrypt 密文写回，历史账号无需重置即可继续使用。
     */
    private User upgradePasswordIfNeeded(User user, String rawPassword) {
        if (user == null || user.getPassword() == null || isBcryptHash(user.getPassword())) {
            return user;
        }
        User update = new User();
        update.setId(user.getId());
        update.setPassword(encryptPassword(rawPassword));
        userMapper.updateById(update);
        user.setPassword(update.getPassword());
        return user;
    }
}
