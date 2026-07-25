package com.kmbeast.pojo.dto;

import com.kmbeast.pojo.entity.Dishes;
import com.kmbeast.pojo.entity.DishesPackage;
import lombok.Data;

import java.util.List;

@Data
public class DishesSaveDTO {

    /**
     * 菜品信息
     */
    private Dishes dishes;

    /**
     * 套餐列表
     */
    private List<DishesPackage> dishesPackageList;
}
