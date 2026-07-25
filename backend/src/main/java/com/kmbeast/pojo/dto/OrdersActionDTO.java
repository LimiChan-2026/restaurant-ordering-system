package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 订单状态操作参数。支付、取消、接单、完成统一使用 ordersId。
 */
@Data
public class OrdersActionDTO {

    @NotNull(message = "订单ID不能为空")
    private Integer ordersId;
}
