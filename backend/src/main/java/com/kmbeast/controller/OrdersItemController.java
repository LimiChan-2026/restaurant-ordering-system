package com.kmbeast.controller;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.OrdersItemQueryDTO;
import com.kmbeast.service.OrdersItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders-item")
@RequiredArgsConstructor
public class OrdersItemController {

    private final OrdersItemService ordersItemService;

    @PostMapping("/list")
    public R list(@Valid @RequestBody OrdersItemQueryDTO queryDTO) {
        return ordersItemService.list(queryDTO);
    }
}
