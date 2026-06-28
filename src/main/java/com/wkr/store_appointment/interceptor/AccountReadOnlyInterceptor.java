package com.wkr.store_appointment.interceptor;

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
import java.util.Set;

@Component
public class AccountReadOnlyInterceptor implements HandlerInterceptor {

    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        if (!WRITE_METHODS.contains(request.getMethod())) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getAdminTokenName());
        if (token == null || token.isEmpty()) {
            return true;
        }

        try {
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            String role = String.valueOf(claims.get(JwtClaimsConstant.ROLE));
            if ("readonly".equalsIgnoreCase(role) || "viewer".equalsIgnoreCase(role) || "read_only".equalsIgnoreCase(role)) {
                writeForbidden(response, "当前账号为只读账号，不能修改数据");
                return false;
            }
        } catch (Exception ignored) {
            return true;
        }

        return true;
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":0,\"msg\":\"" + message + "\"}");
    }
}
