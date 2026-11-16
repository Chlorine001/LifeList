package com.lifelist.common;

/**
 * 当前登录用户上下文
 * 用 ThreadLocal 存「当前请求是哪个用户在操作」，
 * 拦截器在进入 Controller 前写入，请求结束后清理
 *
 * 这是数据隔离的核心：业务代码里所有查询都带上 UserContext.getUserId()
 */
public class UserContext {

    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();

    public static void set(Long userId) {
        HOLDER.set(userId);
    }

    public static Long get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
