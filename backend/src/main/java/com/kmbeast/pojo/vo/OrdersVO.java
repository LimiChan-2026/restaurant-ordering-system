package com.kmbeast.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrdersVO {
    private Integer id;
    private String code;
    private Integer userId;
    private Integer dishesTableId;
    private Integer status;
    private BigDecimal totalPrice;
    private LocalDateTime serveFoodTime;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private String username;
    private String dishesTableNumber;
}
