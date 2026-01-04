package com.polo.Blog.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    private static final String SECRET_STRING = "TravelManagerSecretkeyanwoepnwswjolkgn";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION = 1000 * 60 * 60 * 24;

    /**
     * 生成 Token (修改：增加 roleKey 参数)
     * @param username 用户名
     * @param roleKey 角色标识 (如 admin)
     */
    public static String generateToken(String username, String roleKey) {
        // 你可以把 role 放在 map 里，也可以直接用 .claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roleKey);

        return Jwts.builder()
                .subject(username) // 标准字段：存用户名
                .claims(claims)    // 【核心】存入自定义权限字段
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 Token
     * @return 如果解析成功返回 Claims，失败返回 null
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY) // 设置验签的 Key (新版写法)
                    .build()
                    .parseSignedClaims(token) // 解析
                    .getPayload(); // 获取载荷 (旧版叫 getBody，新版叫 getPayload)
        } catch (Exception e) {
            // Token 过期或被篡改，返回 null
            System.out.println("Token 解析失败: " + e.getMessage());
            return null;
        }
    }
}
