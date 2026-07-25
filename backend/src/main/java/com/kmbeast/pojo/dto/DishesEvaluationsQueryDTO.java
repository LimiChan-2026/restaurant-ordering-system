package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class DishesEvaluationsQueryDTO {
    private Integer current = 0;
    private Integer size = 10;
    private Integer dishesId;
    private Integer userId;
}
