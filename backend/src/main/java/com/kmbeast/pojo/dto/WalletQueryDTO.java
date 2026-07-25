package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class WalletQueryDTO {

    private Integer current = 0;
    private Integer size = 10;
    private String username;
    private Integer type;
    private Boolean status;
}
