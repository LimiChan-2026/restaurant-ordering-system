package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class OrdersQueryDTO {

    /** 当前页码，从 0 开始。 */
    private Integer current = 0;

    /** 每页条数。 */
    private Integer size = 10;

    private Integer status;
    private String code;
}
