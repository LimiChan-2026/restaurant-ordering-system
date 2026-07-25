package com.kmbeast.controller;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.OrdersActionDTO;
import com.kmbeast.pojo.dto.OrdersQueryDTO;
import com.kmbeast.pojo.dto.OrdersSaveDTO;
import com.kmbeast.service.OrdersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @PostMapping("/saveEntity")
    public R save(@Valid @RequestBody OrdersSaveDTO saveDTO) {
        return ordersService.save(saveDTO);
    }

    @PostMapping("/pay")
    public R pay(@Valid @RequestBody OrdersActionDTO actionDTO) {
        return ordersService.pay(actionDTO);
    }

    @PutMapping("/cancel")
    public R cancel(@Valid @RequestBody OrdersActionDTO actionDTO) {
        return ordersService.cancel(actionDTO);
    }

    @PutMapping("/serve")
    @AdminOnly
    public R serve(@Valid @RequestBody OrdersActionDTO actionDTO) {
        return ordersService.serve(actionDTO);
    }

    @PutMapping("/complete")
    @AdminOnly
    public R complete(@Valid @RequestBody OrdersActionDTO actionDTO) {
        return ordersService.complete(actionDTO);
    }

    @DeleteMapping("/delete/{id}")
    @AdminOnly
    public R delete(@PathVariable Integer id) {
        return ordersService.delete(id);
    }

    @PostMapping("/list")
    @AdminOnly
    public R list(@RequestBody OrdersQueryDTO queryDTO) {
        return ordersService.list(queryDTO);
    }

    @PostMapping("/listUser")
    public R listUser(@RequestBody(required = false) OrdersQueryDTO queryDTO) {
        return ordersService.listUser(queryDTO);
    }
}
