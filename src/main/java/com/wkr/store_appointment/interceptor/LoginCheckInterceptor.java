package com.wkr.store_appointment.interceptor;

import com.wkr.store_appointment.common.BaseContext;
import com.wkr.store_appointment.common.JwtClaimsConstant;
import com.wkr.store_appointment.properties.JwtProperties;
import com.wkr.store_appointment.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getAdminTokenName());
        if (token == null || token.isEmpty()) {
            writeUnauthorized(response, "请先登录");
            return false;
        }

        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Object empId = claims.get(JwtClaimsConstant.EMP_ID);
            if (empId == null) {
                writeUnauthorized(response, "登录信息无效");
                return false;
            }
            BaseContext.setCurrentId(Long.valueOf(empId.toString()));
            return true;
        } catch (Exception ex) {
            writeUnauthorized(response, "登录已过期，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        BaseContext.removeCurrentId();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":0,\"msg\":\"" + message + "\"}");
    }
}
