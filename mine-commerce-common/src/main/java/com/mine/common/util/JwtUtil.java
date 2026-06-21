package com.mine.common.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "mine-commerce-jwt-secret-key-2024";
    private static final long EXPIRE_HOURS = 24;

    public static String generateToken(Long userId, String username, String role) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("role", role);
        payload.put("exp", new Date(System.currentTimeMillis() + EXPIRE_HOURS * 3600 * 1000).getTime());
        payload.put("iat", new Date().getTime());
        String json = JSONUtil.toJsonStr(payload);
        String base64 = cn.hutool.core.codec.Base64.encode(json);
        String sign = SecureUtil.hmacMd5(SECRET).digestHex(base64);
        return base64 + "." + sign;
    }

    public static Map<String, Object> parseToken(String token) {
        if (token == null || !token.contains(".")) {
            return null;
        }
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 2) return null;
            String base64 = parts[0];
            String sign = parts[1];
            String expectedSign = SecureUtil.hmacMd5(SECRET).digestHex(base64);
            if (!expectedSign.equals(sign)) return null;
            String json = cn.hutool.core.codec.Base64.decodeStr(base64);
            Map<String, Object> payload = JSONUtil.toBean(json, Map.class);
            Long exp = (Long) payload.get("exp");
            if (exp == null || exp < System.currentTimeMillis()) return null;
            return payload;
        } catch (Exception e) {
            return null;
        }
    }

    public static Long getUserId(String token) {
        Map<String, Object> payload = parseToken(token);
        if (payload == null) return null;
        Object userId = payload.get("userId");
        if (userId instanceof Number) return ((Number) userId).longValue();
        return null;
    }

    public static String getRole(String token) {
        Map<String, Object> payload = parseToken(token);
        if (payload == null) return null;
        return (String) payload.get("role");
    }
}
