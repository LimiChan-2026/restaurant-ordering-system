package com.kmbeast.controller;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesTypeQueryDTO;
import com.kmbeast.pojo.entity.DishesType;
import com.kmbeast.service.DishesTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dishes-type")
@RequiredArgsConstructor
public class DishesTypeController {

    private final DishesTypeService dishesTypeService;

    /**
     * 查询菜品种类列表
     */
    @PostMapping("/list")
    public R list(@RequestBody DishesTypeQueryDTO queryDTO) {
        return dishesTypeService.list(queryDTO);
    }

    /**
     * 新增菜品种类
     */
    @PostMapping("/saveEntity")
    @AdminOnly
    public R save(@Valid @RequestBody DishesType dishesType) {
        return dishesTypeService.save(dishesType);
    }

    /**
     * 修改菜品种类
     */
    @PutMapping("/updateEntity")
    @AdminOnly
    public R update(@Valid @RequestBody DishesType dishesType) {
        return dishesTypeService.update(dishesType);
    }

    /**
     * 删除菜品种类
     */
    @DeleteMapping("/delete/{id}")
    @AdminOnly
    public R delete(@PathVariable Integer id) {
        return dishesTypeService.delete(id);
    }
}
