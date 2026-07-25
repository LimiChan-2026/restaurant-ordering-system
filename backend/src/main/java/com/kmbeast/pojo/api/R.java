package com.kmbeast.pojo.api;

import lombok.Data;

/**
 * 统一响应结果类
 */
@Data
public class R<T> {

    /** 状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 数据总数（分页时使用） */
    private Long count;

    /**
     * 成功响应（无数据）
     */
    public static <T> R<T> ok() {
        return ok(null, "操作成功");
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> R<T> ok(String message) {
        return ok(null, message);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> R<T> ok(T data) {
        return ok(data, "操作成功");
    }

    /**
     * 成功响应（带数据和消息）
     */
    public static <T> R<T> ok(T data, String message) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

    /**
     * 成功响应（带数据和总数 - 分页）
     */
    public static <T> R<T> ok(T data, Long count) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setData(data);
        r.setCount(count);
        return r;
    }

    /**
     * 失败响应
     */
    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }

    /**
     * 自定义状态码失败响应
     */
    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    /**
     * 未授权响应
     */
    public static <T> R<T> unauthorized() {
        return error(401, "未登录或登录已过期");
    }

    /**
     * 无权限响应
     */
    public static <T> R<T> forbidden() {
        return error(403, "无操作权限");
    }
}
