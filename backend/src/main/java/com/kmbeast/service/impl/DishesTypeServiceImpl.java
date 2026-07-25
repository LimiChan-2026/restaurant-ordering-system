package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.mapper.DishesTypeMapper;
import com.kmbeast.mapper.DishesMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesTypeQueryDTO;
import com.kmbeast.pojo.entity.DishesType;
import com.kmbeast.pojo.entity.Dishes;
import com.kmbeast.service.DishesTypeService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DishesTypeServiceImpl implements DishesTypeService {

    private final DishesTypeMapper dishesTypeMapper;
    private final DishesMapper dishesMapper;

    @Override
    public R list(DishesTypeQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new DishesTypeQueryDTO();
        }
        LambdaQueryWrapper<DishesType> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(DishesType::getName, queryDTO.getName());
        }

        wrapper.orderByAsc(DishesType::getId);

        Page<DishesType> page = PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize());
        Page<DishesType> result = dishesTypeMapper.selectPage(page, wrapper);

        return R.ok(result.getRecords(), result.getTotal());
    }

    @Override
    public R save(DishesType dishesType) {
        if (!StringUtils.hasText(dishesType.getName())) {
            return R.error("种类名称不能为空");
        }
        dishesType.setName(dishesType.getName().trim());
        // 检查种类名称是否重复
        LambdaQueryWrapper<DishesType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishesType::getName, dishesType.getName());
        Long count = dishesTypeMapper.selectCount(wrapper);
        if (count > 0) {
            return R.error("种类名称已存在");
        }

        dishesTypeMapper.insert(dishesType);
        return R.ok("新增成功");
    }

    @Override
    public R update(DishesType dishesType) {
        if (dishesType.getId() == null) {
            return R.error("种类ID不能为空");
        }
        if (dishesTypeMapper.selectById(dishesType.getId()) == null) {
            return R.error("种类不存在");
        }
        if (!StringUtils.hasText(dishesType.getName())) {
            return R.error("种类名称不能为空");
        }
        dishesType.setName(dishesType.getName().trim());

        // 检查种类名称是否重复（排除自身）
        LambdaQueryWrapper<DishesType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishesType::getName, dishesType.getName())
                .ne(DishesType::getId, dishesType.getId());
        Long count = dishesTypeMapper.selectCount(wrapper);
        if (count > 0) {
            return R.error("种类名称已存在");
        }

        dishesTypeMapper.updateById(dishesType);
        return R.ok("修改成功");
    }

    @Override
    public R delete(Integer id) {
        if (dishesMapper.selectCount(new LambdaQueryWrapper<Dishes>().eq(Dishes::getTypeId, id)) > 0) {
            return R.error("该种类下存在菜品，无法删除");
        }
        if (dishesTypeMapper.deleteById(id) == 0) {
            return R.error("种类不存在");
        }
        return R.ok("删除成功");
    }
}
