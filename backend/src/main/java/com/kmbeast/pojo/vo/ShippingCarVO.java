package com.kmbeast.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShippingCarVO {

    private Integer id;
    private Integer userId;
    private Integer dishesPackageId;
    private Integer plusNumber;
    private Boolean isSelected;
    private LocalDateTime createTime;
    private String dishesPackageName;
    private BigDecimal dishesPackagePrice;
    private String dishesName;
    private String dishesCover;
}
