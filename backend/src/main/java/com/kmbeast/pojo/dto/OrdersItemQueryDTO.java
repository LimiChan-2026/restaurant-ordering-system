package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrdersItemQueryDTO {

    @NotNull(message = "订单ID不能为空")
    private Integer ordersId;
}
