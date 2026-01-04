package com.polo.Blog.Interceptor;

import com.polo.Blog.Utils.JwtUtils; // 假设你有这个工具类
import com.polo.Blog.Utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 放行 OPTIONS 请求 (关键！否则跨域会报错)
        // 浏览器发正式请求前会发一个 OPTIONS 预检请求，这个请求没有 Token，必须直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 获取 Token
        String token = request.getHeader("Authorization"); // 前端 request.ts 里设置的 Key

        // 3. 校验 Token 是否存在
        if (!StringUtils.hasText(token)) {
            response.setStatus(401); // 设置状态码
            return false; // 拦截，不让进 Controller
        }

        try {
            // 4. 解析 Token (这里会抛异常如果 Token 过期或被篡改)
            // 假设你的 Token 载荷(Claims)里存的是 Subject = username
            Claims claims = JwtUtils.parseToken(token);
            String username = null;
            String roleKey = null;
            if (claims != null) {
                username = claims.getSubject();
                roleKey = claims.get("role", String.class);
            }

            // 5. 存入 ThreadLocal，供后续 Controller 使用
            UserContext.set(new UserContext.LoginUser(username, roleKey));
            return true; // 放行
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 6. 必须清理 ThreadLocal，防止内存泄漏
        UserContext.remove();
    }
}

