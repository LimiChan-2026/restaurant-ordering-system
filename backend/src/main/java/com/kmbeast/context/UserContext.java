package com.kmbeast.context;

/**
 * 用户上下文（使用ThreadLocal存储当前登录用户信息）
 */
public class UserContext {

    private static final ThreadLocal<Integer> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> ACCOUNT = new ThreadLocal<>();
    private static final ThreadLocal<Integer> ROLE = new ThreadLocal<>();

    /**
     * 设置用户ID
     */
    public static void setUserId(Integer userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取用户ID
     */
    public static Integer getUserId() {
        return USER_ID.get();
    }

    /**
     * 设置用户账号
     */
    public static void setAccount(String account) {
        ACCOUNT.set(account);
    }

    /**
     * 获取用户账号
     */
    public static String getAccount() {
        return ACCOUNT.get();
    }

    /**
     * 设置用户角色
     */
    public static void setRole(Integer role) {
        ROLE.set(role);
    }

    /**
     * 获取用户角色
     */
    public static Integer getRole() {
        return ROLE.get();
    }

    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin() {
        Integer role = ROLE.get();
        return role != null && role == 2;
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        USER_ID.remove();
        ACCOUNT.remove();
        ROLE.remove();
    }
}
