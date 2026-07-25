package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class DishesPackageQueryDTO {

    /**
     * 当前页码（从0开始）
     */
    private Integer current = 0;

    /**
     * 每页条数
     */
    private Integer size = 10;

    /**
     * 菜品ID
     */
    private Integer dishesId;
}
