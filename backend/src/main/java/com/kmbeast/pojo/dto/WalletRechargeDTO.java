package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletRechargeDTO {

    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "1", message = "充值金额必须为正整数")
    @Digits(integer = 10, fraction = 0, message = "充值金额必须为整数")
    private BigDecimal money;
}
