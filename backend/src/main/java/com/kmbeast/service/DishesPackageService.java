package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesPackageQueryDTO;
import com.kmbeast.pojo.entity.DishesPackage;

public interface DishesPackageService {

    /**
     * 查询套餐列表
     */
    R list(DishesPackageQueryDTO queryDTO);

    /**
     * 新增套餐
     */
    R save(DishesPackage dishesPackage);

    /**
     * 修改套餐
     */
    R update(DishesPackage dishesPackage);

    /**
     * 删除套餐
     */
    R delete(Integer id);
}
