package com.kmbeast.controller;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.ShippingCarQueryDTO;
import com.kmbeast.pojo.dto.ShippingCarSaveDTO;
import com.kmbeast.pojo.dto.ShippingCarUpdateDTO;
import com.kmbeast.service.ShippingCarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipping-car")
@RequiredArgsConstructor
public class ShippingCarController {

    private final ShippingCarService shippingCarService;

    @PostMapping("/saveEntity")
    public R save(@Valid @RequestBody ShippingCarSaveDTO saveDTO) {
        return shippingCarService.save(saveDTO);
    }

    @PostMapping("/listUser")
    public R listUser(@RequestBody(required = false) ShippingCarQueryDTO queryDTO) {
        return shippingCarService.listUser(queryDTO == null ? new ShippingCarQueryDTO() : queryDTO);
    }

    @PutMapping("/updateEntity")
    public R update(@Valid @RequestBody ShippingCarUpdateDTO updateDTO) {
        return shippingCarService.update(updateDTO);
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Integer id) {
        return shippingCarService.delete(id);
    }
}
