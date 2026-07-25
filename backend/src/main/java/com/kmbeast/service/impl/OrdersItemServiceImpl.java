package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.OrdersItemMapper;
import com.kmbeast.mapper.OrdersMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.OrdersItemQueryDTO;
import com.kmbeast.pojo.entity.Orders;
import com.kmbeast.pojo.entity.OrdersItem;
import com.kmbeast.service.OrdersItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdersItemServiceImpl implements OrdersItemService {

    private final OrdersMapper ordersMapper;
    private final OrdersItemMapper ordersItemMapper;

    @Override
    public R list(OrdersItemQueryDTO queryDTO) {
        Orders order = ordersMapper.selectById(queryDTO.getOrdersId());
        if (order == null) {
            return R.error("订单不存在");
        }
        if (!UserContext.isAdmin() && !order.getUserId().equals(UserContext.getUserId())) {
            return R.forbidden();
        }
        LambdaQueryWrapper<OrdersItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrdersItem::getOrdersId, queryDTO.getOrdersId())
                .orderByAsc(OrdersItem::getId);
        List<OrdersItem> items = ordersItemMapper.selectList(wrapper);
        return R.ok(items);
    }
}
