package com.yaozhidao.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaozhidao.common.Result;
import com.yaozhidao.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证拦截器，认证分三级：
 * 1. 公开路径（PUBLIC_PATHS）：无需 token
 * 2. 可选登录路径（OPTIONAL_AUTH_PATHS）：有合法 token 则解析进上下文，无则放行（如文章浏览）
 * 3. 必须登录路径（其余）：无 token 或 token 非法 → 401
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/v1/auth/**",
            "/api/v1/health",
            "/api/v1/drugs/**",
            "/api/v1/hospitals/**",
            "/api/v1/feedback"
    );

    private static final List<String> OPTIONAL_AUTH_PATHS = Arrays.asList(
            "/api/v1/articles/**"
    );

    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 拦截器独立使用的 Jackson2 ObjectMapper（静态实例，不依赖 Spring bean）。
     * 注意：Spring Boot 4 自动配置的是 Jackson3（tools.jackson），不提供 Jackson2 的 ObjectMapper bean
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // CORS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        boolean optional = matches(path, OPTIONAL_AUTH_PATHS);

        // 公开路径放行，但若有 token 且有效仍解析进上下文
        // （如 /api/v1/drugs/{id}/collect 在公开名单内但需要登录，service 层校验用户）
        if (matches(path, PUBLIC_PATHS)) {
            String pubToken = extractToken(request);
            if (pubToken != null) {
                Long uid = jwtUtil.parseUserId(pubToken);
                if (uid != null) {
                    UserContext.set(uid);
                }
            }
            return true;
        }

        // 可选登录：无 token 直接放行
        String token = extractToken(request);
        if (token == null) {
            if (optional) {
                return true;
            }
            return reject(response, ResultCode.NOT_LOGIN);
        }

        Long userId = jwtUtil.parseUserId(token);
        if (userId == null) {
            if (optional) {
                // 可选路径上 token 非法：放行，按未登录处理
                return true;
            }
            return reject(response, ResultCode.TOKEN_EXPIRED);
        }

        UserContext.set(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 防止 ThreadLocal 泄漏到线程池复用的下一个请求
        UserContext.clear();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return header.substring(BEARER_PREFIX.length()).trim();
    }

    /** 简单通配匹配：/a/b/c 匹配 /a/** 或 /a/b/c */
    private boolean matches(String path, List<String> patterns) {
        for (String p : patterns) {
            if (p.endsWith("/**")) {
                if (path.startsWith(p.substring(0, p.length() - 3))) {
                    return true;
                }
            } else if (p.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private boolean reject(HttpServletResponse response, ResultCode rc) throws Exception {
        response.setStatus(rc.getHttpStatus());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(Result.error(rc)));
        return false;
    }
}
