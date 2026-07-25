package com.kmbeast.pojo.vo;

import lombok.Data;

@Data
public class CollectionVO {
    private Integer id;
    private Integer dishesId;
    private String dishesName;
    private String dishesDetail;
    private String dishesCover;
    private Boolean dishesStatus;
}
