package com.kmbeast.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalletInfoQueryDTO {

    private Integer current = 0;
    private Integer size = 10;

    @NotNull(message = "钱包ID不能为空")
    private Integer walletId;
}
