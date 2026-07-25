package com.kmbeast.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.mapper.DishesMapper;
import com.kmbeast.mapper.OrdersMapper;
import com.kmbeast.mapper.WalletMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DishesMapper dishesMapper;
    private final OrdersMapper ordersMapper;
    private final WalletMapper walletMapper;

    @AdminOnly
    @GetMapping("/dishes-type-count")
    public R<List<Map<String, Object>>> dishesTypeCount() {
        return R.ok(dishesMapper.selectTypeCounts());
    }

    @AdminOnly
    @GetMapping("/order-sales")
    public R<List<Map<String, Object>>> orderSales(@RequestParam(defaultValue = "7") int days) {
        int normalizedDays = Math.min(Math.max(days, 1), 90);
        LocalDate start = LocalDate.now().minusDays(normalizedDays - 1L);
        Map<LocalDate, BigDecimal> salesByDay = new TreeMap<>();
        for (int index = 0; index < normalizedDays; index++) {
            salesByDay.put(start.plusDays(index), BigDecimal.ZERO);
        }
        for (Map<String, Object> row : ordersMapper.selectSalesByDay(start.atStartOfDay())) {
            Object dateValue = row.get("date");
            Object amountValue = row.get("value");
            if (dateValue != null && amountValue != null) {
                salesByDay.put(LocalDate.parse(dateValue.toString()), new BigDecimal(amountValue.toString()));
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        salesByDay.forEach((date, value) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("value", value);
            result.add(item);
        });
        return R.ok(result);
    }

    @AdminOnly
    @GetMapping("/merchant-wallet")
    public R<Wallet> merchantWallet() {
        return R.ok(walletMapper.selectOne(new LambdaQueryWrapper<Wallet>()
                .eq(Wallet::getType, 2)
                .orderByAsc(Wallet::getId)
                .last("limit 1")));
    }
}
