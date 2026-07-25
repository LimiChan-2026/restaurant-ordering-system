package com.kmbeast.service.impl;

import com.kmbeast.mapper.DishesMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.mapper.DishesPackageMapper;
import com.kmbeast.mapper.DishesTableMapper;
import com.kmbeast.mapper.OrdersItemMapper;
import com.kmbeast.mapper.OrdersMapper;
import com.kmbeast.mapper.OrdersRefundReplyMapper;
import com.kmbeast.mapper.ShippingCarMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.OrderItemDTO;
import com.kmbeast.pojo.dto.OrdersActionDTO;
import com.kmbeast.pojo.dto.OrdersSaveDTO;
import com.kmbeast.pojo.dto.OrdersQueryDTO;
import com.kmbeast.pojo.entity.Dishes;
import com.kmbeast.pojo.entity.DishesPackage;
import com.kmbeast.pojo.entity.DishesTable;
import com.kmbeast.pojo.entity.Orders;
import com.kmbeast.pojo.entity.OrdersItem;
import com.kmbeast.pojo.entity.OrdersRefundReply;
import com.kmbeast.pojo.entity.ShippingCar;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.pojo.vo.OrdersVO;
import com.kmbeast.mapper.UserMapper;
import com.kmbeast.context.UserContext;
import com.kmbeast.service.OrdersService;
import com.kmbeast.service.WalletService;
import com.kmbeast.service.MessageService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    private static final Integer STATUS_PENDING_PAYMENT = 1;
    private static final Integer STATUS_PAID = 2;
    private static final Integer STATUS_SERVING = 3;
    private static final Integer STATUS_COMPLETED = 4;
    private static final Integer STATUS_CANCELLED = 5;
    private static final DateTimeFormatter ORDER_CODE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final OrdersMapper ordersMapper;
    private final OrdersItemMapper ordersItemMapper;
    private final OrdersRefundReplyMapper ordersRefundReplyMapper;
    private final DishesPackageMapper dishesPackageMapper;
    private final DishesMapper dishesMapper;
    private final DishesTableMapper dishesTableMapper;
    private final UserMapper userMapper;
    private final ShippingCarMapper shippingCarMapper;
    private final WalletService walletService;
    private final MessageService messageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R save(OrdersSaveDTO saveDTO) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }

        DishesTable dishesTable = dishesTableMapper.selectById(saveDTO.getDishesTableId());
        if (dishesTable == null || !Boolean.TRUE.equals(dishesTable.getStatus())
                || Boolean.TRUE.equals(dishesTable.getOccupied())) {
            return R.error("餐桌不存在或不可用");
        }

        Map<Integer, Integer> itemQuantities = saveDTO.getOrderItemDtoList().stream()
                .collect(Collectors.toMap(OrderItemDTO::getDishesPackageId, OrderItemDTO::getBuyNumber, Integer::sum));
        List<DishesPackage> packages = dishesPackageMapper.selectBatchIds(itemQuantities.keySet());
        if (packages.size() != itemQuantities.size()) {
            return R.error("菜品套餐不存在");
        }
        Map<Integer, DishesPackage> packageById = packages.stream()
                .collect(Collectors.toMap(DishesPackage::getId, Function.identity()));
        Set<Integer> dishesIds = packages.stream().map(DishesPackage::getDishesId).collect(Collectors.toSet());
        Map<Integer, Dishes> dishesById = dishesMapper.selectBatchIds(dishesIds).stream()
                .collect(Collectors.toMap(Dishes::getId, Function.identity()));

        for (Map.Entry<Integer, Integer> item : itemQuantities.entrySet()) {
            DishesPackage dishesPackage = packageById.get(item.getKey());
            if (dishesPackage == null) {
                return R.error("菜品套餐不存在");
            }
            Dishes dishes = dishesById.get(dishesPackage.getDishesId());
            if (dishes == null || !Boolean.TRUE.equals(dishes.getStatus())) {
                return R.error("菜品不存在或已下架");
            }
            if (dishesPackage.getPrice() == null || dishesPackage.getPrice().signum() < 0) {
                return R.error("菜品套餐价格异常");
            }
        }

        if (dishesTableMapper.occupyIfAvailable(dishesTable.getId()) == 0) {
            return R.error("餐桌已被占用或不可用");
        }

        LocalDateTime now = LocalDateTime.now();
        Orders order = new Orders();
        order.setCode(ORDER_CODE_FORMAT.format(now) + ThreadLocalRandom.current().nextInt(100, 1000));
        order.setUserId(userId);
        order.setDishesTableId(dishesTable.getId());
        order.setStatus(STATUS_PENDING_PAYMENT);
        order.setTotalPrice(BigDecimal.ZERO);
        order.setCreateTime(now);
        ordersMapper.insert(order);

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> item : itemQuantities.entrySet()) {
            DishesPackage dishesPackage = packageById.get(item.getKey());
            Dishes dishes = dishesById.get(dishesPackage.getDishesId());

            BigDecimal itemTotal = dishesPackage.getPrice().multiply(BigDecimal.valueOf(item.getValue()));
            OrdersItem orderItem = new OrdersItem();
            orderItem.setOrdersId(order.getId());
            orderItem.setDishesPackageId(dishesPackage.getId());
            orderItem.setSnapPrice(dishesPackage.getPrice());
            orderItem.setSnapName(dishes.getName() + "-" + dishesPackage.getName());
            orderItem.setSnapCover(dishes.getCoverUrl());
            orderItem.setBuyNumber(item.getValue());
            orderItem.setTotalPrice(itemTotal);
            orderItem.setCreateTime(now);
            ordersItemMapper.insert(orderItem);
            totalPrice = totalPrice.add(itemTotal);
        }

        order.setTotalPrice(totalPrice);
        ordersMapper.updateById(order);

        // 订单创建成功后，仅移除本次结算所使用的已选购物车商品。
        LambdaQueryWrapper<ShippingCar> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.eq(ShippingCar::getUserId, userId)
                .eq(ShippingCar::getIsSelected, true)
                .in(ShippingCar::getDishesPackageId, itemQuantities.keySet());
        shippingCarMapper.delete(cartWrapper);
        return R.ok("下单成功");
    }

    @Override
    public R pay(OrdersActionDTO actionDTO) {
        Orders order = findOwnedOrder(actionDTO.getOrdersId());
        if (order == null) {
            return R.error("订单不存在或无权操作");
        }
        if (!STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            return orderStatusError();
        }

        return walletService.payOrder(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R cancel(OrdersActionDTO actionDTO) {
        Orders order = findOwnedOrder(actionDTO.getOrdersId());
        if (order == null) {
            return R.error("订单不存在或无权操作");
        }
        if (!STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            return orderStatusError();
        }
        int updated = ordersMapper.cancelIfPending(order.getId(), UserContext.getUserId(),
                STATUS_PENDING_PAYMENT, STATUS_CANCELLED);
        if (updated == 0) {
            return orderStatusError();
        }
        releaseTable(order);
        return R.ok("订单取消成功");
    }

    @Override
    public R serve(OrdersActionDTO actionDTO) {
        Orders order = ordersMapper.selectById(actionDTO.getOrdersId());
        if (order == null) {
            return R.error("订单不存在");
        }
        if (!STATUS_PAID.equals(order.getStatus())) {
            return orderStatusError();
        }
        int updated = ordersMapper.startServing(order.getId(), STATUS_PAID,
                STATUS_SERVING, LocalDateTime.now());
        if (updated == 0) {
            return orderStatusError();
        }
        messageService.notifyUser(order.getUserId(), 2, "订单 " + order.getCode() + " 已进入出餐中状态");
        return R.ok("接单成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R complete(OrdersActionDTO actionDTO) {
        Orders order = ordersMapper.selectById(actionDTO.getOrdersId());
        if (order == null) {
            return R.error("订单不存在");
        }
        if (!STATUS_SERVING.equals(order.getStatus())) {
            return orderStatusError();
        }
        int updated = ordersMapper.transitionStatus(order.getId(), STATUS_SERVING, STATUS_COMPLETED);
        if (updated == 0) {
            return orderStatusError();
        }
        releaseTable(order);
        messageService.notifyUser(order.getUserId(), 2, "订单 " + order.getCode() + " 已完成，感谢您的光临");
        return R.ok("订单已完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R delete(Integer id) {
        Orders order = ordersMapper.selectById(id);
        if (order == null) {
            return R.error("订单不存在");
        }
        if (!UserContext.isAdmin()) {
            return R.forbidden();
        }
        if (!STATUS_PENDING_PAYMENT.equals(order.getStatus()) && !STATUS_CANCELLED.equals(order.getStatus())) {
            return R.error("仅允许删除未支付或已取消订单，财务订单需永久保留");
        }
        Long refundCount = ordersRefundReplyMapper.selectCount(new LambdaQueryWrapper<OrdersRefundReply>()
                .eq(OrdersRefundReply::getOrdersId, id));
        if (refundCount > 0) {
            return R.error("订单已产生退款记录，无法删除");
        }
        if (STATUS_PENDING_PAYMENT.equals(order.getStatus())) {
            releaseTable(order);
        }
        ordersItemMapper.deleteByOrdersId(id);
        ordersMapper.deleteById(id);
        return R.ok("删除成功");
    }

    @Override
    public R list(OrdersQueryDTO queryDTO) {
        return queryOrders(queryDTO, null);
    }

    @Override
    public R listUser(OrdersQueryDTO queryDTO) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }
        return queryOrders(queryDTO == null ? new OrdersQueryDTO() : queryDTO, userId);
    }

    private Orders findOwnedOrder(Integer ordersId) {
        Orders order = ordersMapper.selectById(ordersId);
        if (order == null || !order.getUserId().equals(UserContext.getUserId())) {
            return null;
        }
        return order;
    }

    private R orderStatusError() {
        return R.error(2002, "订单状态异常");
    }

    private void releaseTable(Orders order) {
        if (order.getDishesTableId() == null || dishesTableMapper.releaseIfOccupied(order.getDishesTableId()) == 0) {
            throw new IllegalStateException("餐桌占用状态异常");
        }
    }

    private R queryOrders(OrdersQueryDTO queryDTO, Integer userId) {
        if (queryDTO == null) {
            queryDTO = new OrdersQueryDTO();
        }
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Orders::getUserId, userId);
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Orders::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getCode())) {
            wrapper.like(Orders::getCode, queryDTO.getCode());
        }
        wrapper.orderByDesc(Orders::getCreateTime);

        Page<Orders> result = ordersMapper.selectPage(PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
        List<Orders> records = result.getRecords();
        Set<Integer> userIds = records.stream().map(Orders::getUserId).collect(Collectors.toSet());
        Set<Integer> tableIds = records.stream().map(Orders::getDishesTableId).collect(Collectors.toSet());
        Map<Integer, User> users = userIds.isEmpty() ? Map.of() : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Integer, DishesTable> tables = tableIds.isEmpty() ? Map.of() : dishesTableMapper.selectBatchIds(tableIds).stream()
                .collect(Collectors.toMap(DishesTable::getId, Function.identity()));
        List<OrdersVO> orders = records.stream().map(order -> toOrdersVO(order, users, tables)).toList();
        return R.ok(orders, result.getTotal());
    }

    private OrdersVO toOrdersVO(Orders order, Map<Integer, User> users, Map<Integer, DishesTable> tables) {
        OrdersVO vo = new OrdersVO();
        BeanUtils.copyProperties(order, vo);
        User user = users.get(order.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        DishesTable dishesTable = tables.get(order.getDishesTableId());
        if (dishesTable != null) {
            vo.setDishesTableNumber(dishesTable.getNumber());
        }
        return vo;
    }
}
