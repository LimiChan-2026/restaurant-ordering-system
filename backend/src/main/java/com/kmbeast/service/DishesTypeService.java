package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesTypeQueryDTO;
import com.kmbeast.pojo.entity.DishesType;

public interface DishesTypeService {

    /**
     * 查询菜品种类列表
     */
    R list(DishesTypeQueryDTO queryDTO);

    /**
     * 新增菜品种类
     */
    R save(DishesType dishesType);

    /**
     * 修改菜品种类
     */
    R update(DishesType dishesType);

    /**
     * 删除菜品种类
     */
    R delete(Integer id);
}
