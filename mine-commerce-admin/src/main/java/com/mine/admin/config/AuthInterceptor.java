package com.mine.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mine.common.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            writeError(response, 401, "请先登录");
            return false;
        }

        Map<String, Object> payload = JwtUtil.parseToken(token);
        if (payload == null) {
            writeError(response, 401, "登录已过期，请重新登录");
            return false;
        }

        String role = (String) payload.get("role");
        String uri = request.getRequestURI();

        if (uri.startsWith("/admin/") && !"ADMIN".equals(role)) {
            writeError(response, 403, "无管理员权限");
            return false;
        }

        request.setAttribute("userId", JwtUtil.getUserId(token));
        request.setAttribute("username", payload.get("username"));
        request.setAttribute("role", role);
        return true;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<>();
        result.put("code", status);
        result.put("message", message);
        result.put("data", null);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
