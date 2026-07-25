package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.mapper.DishesTableMapper;
import com.kmbeast.mapper.OrdersMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesTableQueryDTO;
import com.kmbeast.pojo.dto.DishesTableSaveDTO;
import com.kmbeast.pojo.dto.DishesTableUpdateDTO;
import com.kmbeast.pojo.entity.DishesTable;
import com.kmbeast.pojo.entity.Orders;
import com.kmbeast.service.DishesTableService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DishesTableServiceImpl implements DishesTableService {

    private final DishesTableMapper dishesTableMapper;
    private final OrdersMapper ordersMapper;

    @Override
    public R list(DishesTableQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new DishesTableQueryDTO();
        }
        LambdaQueryWrapper<DishesTable> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(queryDTO.getNumber())) {
            wrapper.like(DishesTable::getNumber, queryDTO.getNumber());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(DishesTable::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByAsc(DishesTable::getNumber);
        Page<DishesTable> result = dishesTableMapper.selectPage(PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
        return R.ok(result.getRecords(), result.getTotal());
    }

    @Override
    public R<Void> save(DishesTableSaveDTO saveDTO) {
        String number = saveDTO.getNumber().trim();
        if (existsNumber(number, null)) {
            return R.error("桌号已存在");
        }
        DishesTable dishesTable = new DishesTable();
        dishesTable.setNumber(number);
        dishesTable.setPersonNumber(saveDTO.getPersonNumber());
        dishesTable.setStatus(saveDTO.getStatus());
        dishesTable.setOccupied(false);
        dishesTableMapper.insert(dishesTable);
        return R.ok("新增餐桌成功");
    }

    @Override
    public R<Void> update(DishesTableUpdateDTO updateDTO) {
        DishesTable dishesTable = dishesTableMapper.selectById(updateDTO.getId());
        if (dishesTable == null) {
            return R.error("餐桌不存在");
        }
        if (Boolean.TRUE.equals(dishesTable.getOccupied())) {
            return R.error("餐桌就餐中，无法修改");
        }
        String number = updateDTO.getNumber().trim();
        if (existsNumber(number, dishesTable.getId())) {
            return R.error("桌号已存在");
        }
        dishesTable.setNumber(number);
        dishesTable.setPersonNumber(updateDTO.getPersonNumber());
        dishesTable.setStatus(updateDTO.getStatus());
        dishesTableMapper.updateById(dishesTable);
        return R.ok("修改餐桌成功");
    }

    @Override
    public R<Void> delete(Integer id) {
        DishesTable dishesTable = dishesTableMapper.selectById(id);
        if (dishesTable == null) {
            return R.error("餐桌不存在");
        }
        Long ordersCount = ordersMapper.selectCount(new LambdaQueryWrapper<Orders>().eq(Orders::getDishesTableId, id));
        if (ordersCount > 0) {
            return R.error("餐桌已关联订单，无法删除");
        }
        dishesTableMapper.deleteById(id);
        return R.ok("删除餐桌成功");
    }

    private boolean existsNumber(String number, Integer excludeId) {
        LambdaQueryWrapper<DishesTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishesTable::getNumber, number);
        if (excludeId != null) {
            wrapper.ne(DishesTable::getId, excludeId);
        }
        return dishesTableMapper.selectCount(wrapper) > 0;
    }
}
