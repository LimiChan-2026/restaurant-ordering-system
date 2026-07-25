package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.OrdersActionDTO;
import com.kmbeast.pojo.dto.OrdersQueryDTO;
import com.kmbeast.pojo.dto.OrdersSaveDTO;

public interface OrdersService {
    R save(OrdersSaveDTO saveDTO);

    R pay(OrdersActionDTO actionDTO);

    R cancel(OrdersActionDTO actionDTO);

    R serve(OrdersActionDTO actionDTO);

    R complete(OrdersActionDTO actionDTO);

    R delete(Integer id);

    R list(OrdersQueryDTO queryDTO);

    R listUser(OrdersQueryDTO queryDTO);
}
