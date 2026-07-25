package com.kmbeast.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 分页参数转换工具。
 *
 * <p>接口文档约定 current 从 0 开始，MyBatis Plus 的 Page 从 1 开始，
 * 所有分页查询必须通过该工具转换，避免页码整体错位。</p>
 */
public final class PageUtils {

    private static final long DEFAULT_SIZE = 10L;
    private static final long MAX_SIZE = 100L;

    private PageUtils() {
    }

    public static <T> Page<T> of(Integer current, Integer size) {
        long zeroBasedCurrent = current == null ? 0L : Math.max(current, 0);
        long pageSize = size == null || size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        return new Page<>(zeroBasedCurrent + 1, pageSize);
    }
}
