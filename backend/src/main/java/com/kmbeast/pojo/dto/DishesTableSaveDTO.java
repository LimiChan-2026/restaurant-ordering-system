package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DishesTableSaveDTO {

    @NotBlank(message = "桌号不能为空")
    @Size(max = 20, message = "桌号长度不能超过20个字符")
    private String number;

    @NotNull(message = "就餐人数不能为空")
    @Min(value = 1, message = "就餐人数必须大于0")
    private Integer personNumber;

    @NotNull(message = "餐桌状态不能为空")
    private Boolean status;
}
