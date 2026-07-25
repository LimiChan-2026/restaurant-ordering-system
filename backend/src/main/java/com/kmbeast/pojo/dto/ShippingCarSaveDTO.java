package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShippingCarSaveDTO {

    @NotNull(message = "菜品套餐ID不能为空")
    private Integer dishesPackageId;

    @NotNull(message = "加购数量不能为空")
    @Min(value = 1, message = "加购数量必须大于0")
    private Integer plusNumber;
}
