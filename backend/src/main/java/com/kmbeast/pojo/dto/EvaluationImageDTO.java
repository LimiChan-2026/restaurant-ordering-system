package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EvaluationImageDTO {
    @NotBlank(message = "图片地址不能为空")
    private String pictureUrl;
    private Integer number;
}
