package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.OrdersItemQueryDTO;

public interface OrdersItemService {
    R list(OrdersItemQueryDTO queryDTO);
}
