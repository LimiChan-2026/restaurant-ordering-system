package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class DishesStatusDTO {

    /**
     * 菜品ID
     */
    private Integer id;

    /**
     * 状态：true上架，false下架
     */
    private Boolean status;
}
