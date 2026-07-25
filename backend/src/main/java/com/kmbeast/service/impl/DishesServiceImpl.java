package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.mapper.DishesMapper;
import com.kmbeast.mapper.DishesPackageMapper;
import com.kmbeast.mapper.DishesTypeMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesQueryDTO;
import com.kmbeast.pojo.dto.DishesSaveDTO;
import com.kmbeast.pojo.dto.DishesStatusDTO;
import com.kmbeast.pojo.entity.Dishes;
import com.kmbeast.pojo.entity.DishesPackage;
import com.kmbeast.pojo.entity.DishesType;
import com.kmbeast.pojo.vo.DishesVO;
import com.kmbeast.service.DishesService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DishesServiceImpl implements DishesService {

    private final DishesMapper dishesMapper;
    private final DishesPackageMapper dishesPackageMapper;
    private final DishesTypeMapper dishesTypeMapper;

    @Override
    public R list(DishesQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new DishesQueryDTO();
        }
        LambdaQueryWrapper<Dishes> wrapper = new LambdaQueryWrapper<>();

        // 菜品名称模糊查询
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Dishes::getName, queryDTO.getName());
        }

        // 按种类筛选
        if (queryDTO.getTypeId() != null) {
            wrapper.eq(Dishes::getTypeId, queryDTO.getTypeId());
        }

        // 按状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Dishes::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByDesc(Dishes::getCreateTime);

        // 分页查询
        Page<Dishes> page = PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize());
        Page<Dishes> result = dishesMapper.selectPage(page, wrapper);

        // 转换为VO列表
        List<DishesVO> voList = result.getRecords().stream()
                .map(this::toDishesVO)
                .collect(Collectors.toList());

        return R.ok(voList, result.getTotal());
    }

    @Override
    public R detail(Integer id) {
        Dishes dishes = dishesMapper.selectById(id);
        if (dishes == null) {
            return R.error("菜品不存在");
        }
        return R.ok(toDishesVO(dishes));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R save(DishesSaveDTO saveDTO) {
        Dishes dishes = saveDTO.getDishes();
        if (dishes == null) {
            return R.error("菜品信息不能为空");
        }
        if (!StringUtils.hasText(dishes.getName())) {
            return R.error("菜品名称不能为空");
        }
        if (dishes.getTypeId() == null) {
            return R.error("菜品种类不能为空");
        }
        if (dishesTypeMapper.selectById(dishes.getTypeId()) == null) {
            return R.error("菜品种类不存在");
        }

        // 设置创建时间
        dishes.setCreateTime(LocalDateTime.now());
        // 默认上架
        if (dishes.getStatus() == null) {
            dishes.setStatus(true);
        }

        // 保存菜品
        dishesMapper.insert(dishes);

        // 保存套餐列表
        List<DishesPackage> packageList = saveDTO.getDishesPackageList();
        if (packageList != null && !packageList.isEmpty()) {
            for (DishesPackage pkg : packageList) {
                if (!StringUtils.hasText(pkg.getName())) {
                    return R.error("套餐名称不能为空");
                }
                if (pkg.getPrice() == null || pkg.getPrice().signum() <= 0) {
                    return R.error("套餐价格必须大于0");
                }
                pkg.setDishesId(dishes.getId());
                pkg.setCreateTime(LocalDateTime.now());
                dishesPackageMapper.insert(pkg);
            }
        }

        log.info("菜品新增成功：{}", dishes.getName());
        return R.ok("新增成功");
    }

    @Override
    public R update(Dishes dishes) {
        if (dishes.getId() == null) {
            return R.error("菜品ID不能为空");
        }
        if (dishesMapper.selectById(dishes.getId()) == null) {
            return R.error("菜品不存在");
        }
        if (dishes.getTypeId() != null && dishesTypeMapper.selectById(dishes.getTypeId()) == null) {
            return R.error("菜品种类不存在");
        }
        if (dishesMapper.updateById(dishes) == 0) {
            return R.error("修改失败");
        }
        return R.ok("修改成功");
    }

    @Override
    public R delete(Integer id) {
        Dishes dishes = dishesMapper.selectById(id);
        if (dishes == null) {
            return R.error("菜品不存在");
        }
        if (Boolean.TRUE.equals(dishes.getStatus())) {
            return R.error("请先下架菜品再删除");
        }

        // 同时删除该菜品下的所有套餐
        LambdaQueryWrapper<DishesPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishesPackage::getDishesId, id);
        dishesPackageMapper.delete(wrapper);

        dishesMapper.deleteById(id);

        return R.ok("删除成功");
    }

    @Override
    public R updateStatus(DishesStatusDTO statusDTO) {
        if (statusDTO.getId() == null) {
            return R.error("菜品ID不能为空");
        }
        if (statusDTO.getStatus() == null) {
            return R.error("状态不能为空");
        }

        if (dishesMapper.selectById(statusDTO.getId()) == null) {
            return R.error("菜品不存在");
        }
        Dishes dishes = new Dishes();
        dishes.setId(statusDTO.getId());
        dishes.setStatus(statusDTO.getStatus());
        dishesMapper.updateById(dishes);

        String statusText = statusDTO.getStatus() ? "上架" : "下架";
        log.info("菜品{}成功：id={}", statusText, statusDTO.getId());
        return R.ok(statusText + "成功");
    }

    /**
     * 将Dishes实体转换为DishesVO（包含种类名称和套餐列表）
     */
    private DishesVO toDishesVO(Dishes dishes) {
        DishesVO vo = new DishesVO();
        BeanUtils.copyProperties(dishes, vo);

        // 查询种类名称
        DishesType dishesType = dishesTypeMapper.selectById(dishes.getTypeId());
        if (dishesType != null) {
            vo.setTypeName(dishesType.getName());
        }

        // 查询套餐列表
        LambdaQueryWrapper<DishesPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishesPackage::getDishesId, dishes.getId());
        wrapper.orderByAsc(DishesPackage::getPrice);
        List<DishesPackage> packageList = dishesPackageMapper.selectList(wrapper);
        vo.setDishesPackageList(packageList);

        return vo;
    }
}
