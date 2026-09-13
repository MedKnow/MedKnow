package com.yaozhidao.security;

/**
 * 当前登录用户上下文（ThreadLocal）
 * 由 JwtInterceptor 在请求进入时写入、结束时清理；Service 层通过 getUserId() 取当前用户
 */
public final class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId) {
        CURRENT_USER.set(userId);
    }

    /** 当前登录用户 id；未登录（可选登录接口）返回 null */
    public static Long getUserId() {
        return CURRENT_USER.get();
    }

    /** 必须登录场景下取用户，未登录直接抛业务异常（双保险） */
    public static Long requireUserId() {
        Long userId = CURRENT_USER.get();
        if (userId == null) {
            throw new com.yaozhidao.common.BizException(com.yaozhidao.common.ResultCode.NOT_LOGIN);
        }
        return userId;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
