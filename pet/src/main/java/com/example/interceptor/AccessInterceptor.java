package com.example.interceptor;

import com.google.gson.Gson;
import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.OpLog;
import com.example.entity.User;
import com.example.permission.Access;
import com.example.permission.AccessLevel;
import com.example.service.OpLogService;
import com.example.service.UserService;
import com.example.utils.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 该拦截器有两个用途：1.拦截request记录log 2.接口验权
 */
@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(AccessInterceptor.class);

    private static OpLogService service;

    private static UserService userService;

    @Autowired
    public void setOpLogService( OpLogService service) {
        // 为解决先@Component 后@Autowired失效的方案
        AccessInterceptor.service = service;
    }

    @Autowired
    public void setUserService( UserService userService) {
        // 为解决先@Component 后@Autowired失效的方案
        AccessInterceptor.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        request.setAttribute("_startTime", System.currentTimeMillis());

        //**********************验权代码*************************
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Access access = method.getAnnotation(Access.class);
        if (access == null) {
            // 如果注解为null, 说明不需要拦截, 直接放过
            return true;
        }

        // 管理员
        if(access.level().getCode() == AccessLevel.ADMIN.getCode()) {
            String token = request.getHeader("ADMINTOKEN");
            logger.info("token==>" + token);
            User user = userService.getUserByToken(token);
            if(user != null && user.getRole() != null && user.getRole() == User.AdminUser){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "无操作权限");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        // 管理员或登录用户（同一接口同时服务前台用户与后台管理员）
        if(access.level().getCode() == AccessLevel.ADMIN_OR_LOGIN.getCode()) {
            User admin = userService.getUserByToken(request.getHeader("ADMINTOKEN"));
            if(admin != null && admin.getRole() != null && admin.getRole() == User.AdminUser){
                return true;
            }
            User loginUser = userService.getUserByToken(request.getHeader("TOKEN"));
            if(loginUser != null && loginUser.getRole() != null && loginUser.getRole() == User.NormalUser){
                return true;
            }
            APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "未登录");
            writeResponse(response, apiResponse);
            return false;
        }

        // 用户
        if(access.level().getCode() == AccessLevel.LOGIN.getCode()) {
            String token = request.getHeader("TOKEN");
            logger.info("token==>" + token);
            User user = userService.getUserByToken(token);
            if(user != null && user.getRole() != null && user.getRole() == User.NormalUser){
                return true;
            }else {
                APIResponse apiResponse = new APIResponse(ResponeCode.FAIL, "未登录");
                writeResponse(response, apiResponse);
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        //**********************记录log代码*************************
        Long endTime = System.currentTimeMillis();
        Long startTime = (Long) request.getAttribute("_startTime");
        Long diff = (endTime - startTime);

        OpLog opLog = new OpLog();
        opLog.setReIp(IpUtils.getIpAddr(request));
        opLog.setReMethod(request.getMethod());
        opLog.setReUrl(request.getRequestURI());
        opLog.setReUa(request.getHeader(HttpHeaders.USER_AGENT));
        opLog.setReTime(new Date());
        opLog.setAccessTime(diff.intValue());
        service.createOpLog(opLog);
    }

    public void writeResponse(HttpServletResponse response, APIResponse apiResponse) throws IOException {
        response.setStatus(200);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(apiResponse);
        response.getWriter().println(jsonStr);
        response.getWriter().flush();
    }
}
