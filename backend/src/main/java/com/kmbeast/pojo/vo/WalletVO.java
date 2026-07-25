package com.kmbeast.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WalletVO {

    private Integer id;
    private Integer userId;
    private String username;
    private BigDecimal surplus;
    private Boolean status;
    private Integer type;
    private LocalDateTime createTime;
}
