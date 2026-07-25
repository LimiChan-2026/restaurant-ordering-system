package com.kmbeast.pojo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DishesEvaluationsSaveDTO {
    @Valid
    @NotNull(message = "评价信息不能为空")
    private DishesEvaluationContentDTO dishesEvaluations;
    private List<@Valid EvaluationImageDTO> imagesList;
}
