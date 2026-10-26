package com.example.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.HttpContextUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * MyBatis-Plus 自动填充处理器
 * 在 insert 时自动填充创建人、创建时间、修改人、修改时间
 * 在 update 时自动填充修改人、修改时间
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyMetaObjectHandler.class);

    @Autowired
    private UserService userService;

    @Override
    public void insertFill(MetaObject metaObject) {
        User currentUser = getCurrentUser();
        Long userId = currentUser != null ? Long.valueOf(currentUser.getId()) : null;
        String username = currentUser != null ? currentUser.getUsername() : null;
        Date now = new Date();

        this.strictInsertFill(metaObject, "createBy", Long.class, userId);
        this.strictInsertFill(metaObject, "createByName", String.class, username);
        this.strictInsertFill(metaObject, "createTime", Date.class, now);
        this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
        this.strictInsertFill(metaObject, "updateByName", String.class, username);
        this.strictInsertFill(metaObject, "updateTime", Date.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        User currentUser = getCurrentUser();
        Long userId = currentUser != null ? Long.valueOf(currentUser.getId()) : null;
        String username = currentUser != null ? currentUser.getUsername() : null;

        this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
        this.strictUpdateFill(metaObject, "updateByName", String.class, username);
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }

    /**
     * 从请求上下文中通过 token 获取当前登录用户
     */
    private User getCurrentUser() {
        try {
            HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
            if (request == null) {
                return null;
            }
            // 优先从管理员 token 获取，其次从普通用户 token 获取
            String token = request.getHeader("ADMINTOKEN");
            if (token == null || token.isEmpty()) {
                token = request.getHeader("TOKEN");
            }
            if (token != null && !token.isEmpty()) {
                return userService.getUserByToken(token);
            }
        } catch (Exception e) {
            logger.debug("获取当前用户信息失败", e);
        }
        return null;
    }
}
