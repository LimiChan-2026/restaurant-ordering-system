package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishesEvaluationsReplyDTO {
    @NotNull(message = "评价ID不能为空")
    private Integer id;
    @NotBlank(message = "回复内容不能为空")
    private String replyContent;
}
