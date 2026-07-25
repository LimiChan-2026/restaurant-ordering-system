package com.kmbeast.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.utils.PageUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PageUtilsTest {

    @Test
    void convertsZeroBasedApiPageToMybatisPage() {
        Page<Object> first = PageUtils.of(0, 10);
        Page<Object> second = PageUtils.of(1, 10);

        assertEquals(1, first.getCurrent());
        assertEquals(2, second.getCurrent());
    }

    @Test
    void clampsInvalidPageSize() {
        assertEquals(10, PageUtils.of(-1, 0).getSize());
        assertEquals(100, PageUtils.of(0, 1000).getSize());
    }
}
