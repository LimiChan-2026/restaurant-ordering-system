package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesQueryDTO;
import com.kmbeast.pojo.dto.DishesSaveDTO;
import com.kmbeast.pojo.dto.DishesStatusDTO;
import com.kmbeast.pojo.entity.Dishes;

public interface DishesService {

    /**
     * 查询菜品列表
     */
    R list(DishesQueryDTO queryDTO);

    /**
     * 查询菜品详情
     */
    R detail(Integer id);

    /**
     * 新增菜品及套餐
     */
    R save(DishesSaveDTO saveDTO);

    /**
     * 修改菜品信息
     */
    R update(Dishes dishes);

    /**
     * 删除菜品
     */
    R delete(Integer id);

    /**
     * 菜品上架/下架
     */
    R updateStatus(DishesStatusDTO statusDTO);
}
