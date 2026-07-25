package com.kmbeast.controller;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesQueryDTO;
import com.kmbeast.pojo.dto.DishesSaveDTO;
import com.kmbeast.pojo.dto.DishesStatusDTO;
import com.kmbeast.pojo.entity.Dishes;
import com.kmbeast.service.DishesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishesController {

    private final DishesService dishesService;

    /**
     * 查询菜品列表
     */
    @PostMapping("/list")
    public R list(@RequestBody DishesQueryDTO queryDTO) {
        return dishesService.list(queryDTO);
    }

    /**
     * 查询菜品详情
     */
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Integer id) {
        return dishesService.detail(id);
    }

    /**
     * 新增菜品及套餐
     */
    @PostMapping("/saveEntity")
    @AdminOnly
    public R save(@Valid @RequestBody DishesSaveDTO saveDTO) {
        return dishesService.save(saveDTO);
    }

    /**
     * 修改菜品信息
     */
    @PutMapping("/updateEntity")
    @AdminOnly
    public R update(@Valid @RequestBody Dishes dishes) {
        return dishesService.update(dishes);
    }

    /**
     * 删除菜品
     */
    @DeleteMapping("/delete/{id}")
    @AdminOnly
    public R delete(@PathVariable Integer id) {
        return dishesService.delete(id);
    }

    /**
     * 菜品上架/下架
     */
    @PutMapping("/updateStatus")
    @AdminOnly
    public R updateStatus(@Valid @RequestBody DishesStatusDTO statusDTO) {
        return dishesService.updateStatus(statusDTO);
    }
}
