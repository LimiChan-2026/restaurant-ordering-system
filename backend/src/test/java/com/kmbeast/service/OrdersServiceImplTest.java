package com.kmbeast.service;

import com.kmbeast.mapper.*;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.OrdersActionDTO;
import com.kmbeast.pojo.entity.Orders;
import com.kmbeast.context.UserContext;
import org.junit.jupiter.api.AfterEach;
import com.kmbeast.service.impl.OrdersServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersServiceImplTest {
    @Mock private OrdersMapper ordersMapper; @Mock private OrdersItemMapper ordersItemMapper;
    @Mock private DishesPackageMapper dishesPackageMapper; @Mock private DishesMapper dishesMapper;
    @Mock private DishesTableMapper dishesTableMapper; @Mock private UserMapper userMapper;
    @Mock private OrdersRefundReplyMapper ordersRefundReplyMapper;
    @Mock private ShippingCarMapper shippingCarMapper; @Mock private WalletService walletService;
    @Mock private MessageService messageService; @InjectMocks private OrdersServiceImpl ordersService;

    @Test void serveRejectsAnOrderThatHasNotBeenPaid() {
        Orders order = new Orders(); order.setId(1); order.setStatus(1);
        when(ordersMapper.selectById(1)).thenReturn(order);
        OrdersActionDTO dto = new OrdersActionDTO(); dto.setOrdersId(1);
        R result = ordersService.serve(dto);
        assertEquals(2002, result.getCode()); verify(ordersMapper, never()).updateById(any());
    }

    @Test void completeRejectsAnOrderThatIsNotServing() {
        Orders order = new Orders(); order.setId(1); order.setStatus(2);
        when(ordersMapper.selectById(1)).thenReturn(order);
        OrdersActionDTO dto = new OrdersActionDTO(); dto.setOrdersId(1);
        R result = ordersService.complete(dto);
        assertEquals(2002, result.getCode()); verify(ordersMapper, never()).updateById(any());
    }

    @AfterEach void clearContext() { UserContext.clear(); }

    @Test void cancelUsesConditionalStatusUpdate() {
        UserContext.setUserId(8);
        Orders order = new Orders(); order.setId(1); order.setUserId(8); order.setDishesTableId(2); order.setStatus(1);
        when(ordersMapper.selectById(1)).thenReturn(order);
        when(ordersMapper.cancelIfPending(1, 8, 1, 5)).thenReturn(1);
        when(dishesTableMapper.releaseIfOccupied(2)).thenReturn(1);
        OrdersActionDTO dto = new OrdersActionDTO(); dto.setOrdersId(1);

        R result = ordersService.cancel(dto);

        assertEquals(200, result.getCode());
        verify(ordersMapper).cancelIfPending(1, 8, 1, 5);
        verify(dishesTableMapper).releaseIfOccupied(2);
        verify(ordersMapper, never()).updateById(any());
    }

    @Test void cancelRejectsWhenConcurrentUpdateChangedStatus() {
        UserContext.setUserId(8);
        Orders order = new Orders(); order.setId(1); order.setUserId(8); order.setStatus(1);
        when(ordersMapper.selectById(1)).thenReturn(order);
        when(ordersMapper.cancelIfPending(1, 8, 1, 5)).thenReturn(0);
        OrdersActionDTO dto = new OrdersActionDTO(); dto.setOrdersId(1);

        R result = ordersService.cancel(dto);

        assertEquals(2002, result.getCode());
    }
}
