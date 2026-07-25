package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class DishesTypeQueryDTO {

    /**
     * 当前页码（从0开始）
     */
    private Integer current = 0;

    /**
     * 每页条数
     */
    private Integer size = 10;

    /**
     * 种类名称（模糊查询）
     */
    private String name;
}
