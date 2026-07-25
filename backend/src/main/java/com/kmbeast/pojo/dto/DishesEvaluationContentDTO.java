package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishesEvaluationContentDTO {
    @NotNull(message = "菜品ID不能为空")
    private Integer dishesId;
    @NotBlank(message = "评价内容不能为空")
    private String content;
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分必须在1到5之间")
    @Max(value = 5, message = "评分必须在1到5之间")
    private Integer ratingValue;
}
