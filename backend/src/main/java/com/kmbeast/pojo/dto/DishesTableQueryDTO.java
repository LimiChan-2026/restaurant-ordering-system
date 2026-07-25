package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class DishesTableQueryDTO {
    private Integer current = 0;
    private Integer size = 10;
    private String number;
    private Boolean status;
}
