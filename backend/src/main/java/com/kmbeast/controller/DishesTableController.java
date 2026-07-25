package com.kmbeast.controller;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesTableQueryDTO;
import com.kmbeast.pojo.dto.DishesTableSaveDTO;
import com.kmbeast.pojo.dto.DishesTableUpdateDTO;
import com.kmbeast.service.DishesTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dishes-table")
@RequiredArgsConstructor
public class DishesTableController {

    private final DishesTableService dishesTableService;

    @PostMapping("/list")
    public R list(@RequestBody(required = false) DishesTableQueryDTO queryDTO) {
        return dishesTableService.list(queryDTO == null ? new DishesTableQueryDTO() : queryDTO);
    }

    @AdminOnly
    @PostMapping("/saveEntity")
    public R<Void> save(@Valid @RequestBody DishesTableSaveDTO saveDTO) {
        return dishesTableService.save(saveDTO);
    }

    @AdminOnly
    @PutMapping("/updateEntity")
    public R<Void> update(@Valid @RequestBody DishesTableUpdateDTO updateDTO) {
        return dishesTableService.update(updateDTO);
    }

    @AdminOnly
    @DeleteMapping("/delete/{id}")
    public R<Void> delete(@PathVariable Integer id) {
        return dishesTableService.delete(id);
    }
}
