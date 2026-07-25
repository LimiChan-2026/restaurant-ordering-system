package com.kmbeast.pojo.vo;

import com.kmbeast.pojo.entity.DishesPackage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DishesVO {

    private Integer id;

    private Integer typeId;

    private String name;

    private String detail;

    private String coverUrl;

    private Boolean status;

    private LocalDateTime createTime;

    /**
     * 种类名称（关联查询）
     */
    private String typeName;

    /**
     * 套餐列表（关联查询）
     */
    private List<DishesPackage> dishesPackageList;
}
