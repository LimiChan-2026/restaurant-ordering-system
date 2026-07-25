package com.kmbeast.pojo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrdersSaveDTO {

    @NotNull(message = "餐桌ID不能为空")
    private Integer dishesTableId;

    @Valid
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemDTO> orderItemDtoList;
}
