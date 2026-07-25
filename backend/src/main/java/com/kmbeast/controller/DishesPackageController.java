package com.kmbeast.controller;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesPackageQueryDTO;
import com.kmbeast.pojo.entity.DishesPackage;
import com.kmbeast.service.DishesPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dishes-package")
@RequiredArgsConstructor
public class DishesPackageController {

    private final DishesPackageService dishesPackageService;

    /**
     * 查询套餐列表
     */
    @PostMapping("/list")
    public R list(@RequestBody DishesPackageQueryDTO queryDTO) {
        return dishesPackageService.list(queryDTO);
    }

    /**
     * 新增套餐
     */
    @PostMapping("/saveEntity")
    @AdminOnly
    public R save(@Valid @RequestBody DishesPackage dishesPackage) {
        return dishesPackageService.save(dishesPackage);
    }

    /**
     * 修改套餐
     */
    @PutMapping("/updateEntity")
    @AdminOnly
    public R update(@Valid @RequestBody DishesPackage dishesPackage) {
        return dishesPackageService.update(dishesPackage);
    }

    /**
     * 删除套餐
     */
    @DeleteMapping("/delete/{id}")
    @AdminOnly
    public R delete(@PathVariable Integer id) {
        return dishesPackageService.delete(id);
    }
}
