package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishesIdDTO {
    @NotNull(message = "菜品ID不能为空")
    private Integer dishesId;
}
