package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class ShippingCarQueryDTO {

    private Integer current = 0;
    private Integer size = 100;
    private Boolean isSelected;
}
