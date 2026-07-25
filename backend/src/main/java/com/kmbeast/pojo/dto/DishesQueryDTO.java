package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class DishesQueryDTO {

    /**
     * 当前页码（从0开始）
     */
    private Integer current = 0;

    /**
     * 每页条数
     */
    private Integer size = 10;

    /**
     * 菜品名称（模糊查询）
     */
    private String name;

    /**
     * 菜品种类ID
     */
    private Integer typeId;

    /**
     * 状态：0下架，1上架
     */
    private Boolean status;
}
