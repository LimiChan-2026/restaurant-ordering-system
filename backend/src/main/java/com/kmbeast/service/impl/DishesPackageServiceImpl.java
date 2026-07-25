package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.mapper.DishesPackageMapper;
import com.kmbeast.mapper.DishesMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesPackageQueryDTO;
import com.kmbeast.pojo.entity.DishesPackage;
import com.kmbeast.service.DishesPackageService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DishesPackageServiceImpl implements DishesPackageService {

    private final DishesPackageMapper dishesPackageMapper;
    private final DishesMapper dishesMapper;

    @Override
    public R list(DishesPackageQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new DishesPackageQueryDTO();
        }
        LambdaQueryWrapper<DishesPackage> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getDishesId() != null) {
            wrapper.eq(DishesPackage::getDishesId, queryDTO.getDishesId());
        }

        wrapper.orderByAsc(DishesPackage::getId);

        Page<DishesPackage> page = PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize());
        Page<DishesPackage> result = dishesPackageMapper.selectPage(page, wrapper);

        return R.ok(result.getRecords(), result.getTotal());
    }

    @Override
    public R save(DishesPackage dishesPackage) {
        // 验证必填字段
        if (dishesPackage.getDishesId() == null) {
            return R.error("菜品ID不能为空");
        }
        if (dishesMapper.selectById(dishesPackage.getDishesId()) == null) {
            return R.error("菜品不存在");
        }
        if (!StringUtils.hasText(dishesPackage.getName())) {
            return R.error("套餐名称不能为空");
        }
        if (dishesPackage.getPrice() == null || dishesPackage.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("价格必须大于0");
        }

        dishesPackage.setCreateTime(LocalDateTime.now());
        dishesPackageMapper.insert(dishesPackage);
        return R.ok("新增成功");
    }

    @Override
    public R update(DishesPackage dishesPackage) {
        if (dishesPackage.getId() == null) {
            return R.error("套餐ID不能为空");
        }
        if (dishesPackageMapper.selectById(dishesPackage.getId()) == null) {
            return R.error("套餐不存在");
        }
        if (dishesPackage.getDishesId() != null && dishesMapper.selectById(dishesPackage.getDishesId()) == null) {
            return R.error("菜品不存在");
        }
        if (dishesPackage.getName() != null && !StringUtils.hasText(dishesPackage.getName())) {
            return R.error("套餐名称不能为空");
        }
        if (dishesPackage.getPrice() != null && dishesPackage.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("价格必须大于0");
        }

        dishesPackageMapper.updateById(dishesPackage);
        return R.ok("修改成功");
    }

    @Override
    public R delete(Integer id) {
        if (dishesPackageMapper.deleteById(id) == 0) {
            return R.error("套餐不存在");
        }
        return R.ok("删除成功");
    }
}
