package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.DishesMapper;
import com.kmbeast.mapper.DishesPackageMapper;
import com.kmbeast.mapper.ShippingCarMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.ShippingCarQueryDTO;
import com.kmbeast.pojo.dto.ShippingCarSaveDTO;
import com.kmbeast.pojo.dto.ShippingCarUpdateDTO;
import com.kmbeast.pojo.entity.Dishes;
import com.kmbeast.pojo.entity.DishesPackage;
import com.kmbeast.pojo.entity.ShippingCar;
import com.kmbeast.pojo.vo.ShippingCarVO;
import com.kmbeast.service.ShippingCarService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShippingCarServiceImpl implements ShippingCarService {

    private final ShippingCarMapper shippingCarMapper;
    private final DishesPackageMapper dishesPackageMapper;
    private final DishesMapper dishesMapper;

    @Override
    public R save(ShippingCarSaveDTO saveDTO) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }
        DishesPackage dishesPackage = dishesPackageMapper.selectById(saveDTO.getDishesPackageId());
        if (dishesPackage == null) {
            return R.error("菜品套餐不存在");
        }
        Dishes dishes = dishesMapper.selectById(dishesPackage.getDishesId());
        if (dishes == null || !Boolean.TRUE.equals(dishes.getStatus())) {
            return R.error("菜品不存在或已下架");
        }

        shippingCarMapper.addOrIncrement(userId, saveDTO.getDishesPackageId(),
                saveDTO.getPlusNumber(), LocalDateTime.now());
        return R.ok("加购成功");
    }

    @Override
    public R listUser(ShippingCarQueryDTO queryDTO) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }
        if (queryDTO == null) {
            queryDTO = new ShippingCarQueryDTO();
        }
        LambdaQueryWrapper<ShippingCar> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShippingCar::getUserId, userId);
        if (queryDTO.getIsSelected() != null) {
            wrapper.eq(ShippingCar::getIsSelected, queryDTO.getIsSelected());
        }
        wrapper.orderByDesc(ShippingCar::getCreateTime);
        Page<ShippingCar> result = shippingCarMapper.selectPage(PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
        List<ShippingCarVO> records = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return R.ok(records, result.getTotal());
    }

    @Override
    public R update(ShippingCarUpdateDTO updateDTO) {
        ShippingCar shippingCar = findOwnedCart(updateDTO.getId());
        if (shippingCar == null) {
            return R.error("购物车商品不存在或无权操作");
        }
        if (!shippingCar.getDishesPackageId().equals(updateDTO.getDishesPackageId())) {
            return R.error("菜品套餐不匹配");
        }
        shippingCar.setPlusNumber(updateDTO.getPlusNumber());
        shippingCar.setIsSelected(updateDTO.getIsSelected());
        shippingCarMapper.updateById(shippingCar);
        return R.ok("修改成功");
    }

    @Override
    public R delete(Integer id) {
        ShippingCar shippingCar = findOwnedCart(id);
        if (shippingCar == null) {
            return R.error("购物车商品不存在或无权操作");
        }
        shippingCarMapper.deleteById(id);
        return R.ok("删除成功");
    }

    private ShippingCar findOwnedCart(Integer id) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return null;
        }
        ShippingCar shippingCar = shippingCarMapper.selectById(id);
        return shippingCar != null && userId.equals(shippingCar.getUserId()) ? shippingCar : null;
    }

    private ShippingCarVO toVO(ShippingCar shippingCar) {
        ShippingCarVO vo = new ShippingCarVO();
        BeanUtils.copyProperties(shippingCar, vo);
        DishesPackage dishesPackage = dishesPackageMapper.selectById(shippingCar.getDishesPackageId());
        if (dishesPackage != null) {
            vo.setDishesPackageName(dishesPackage.getName());
            vo.setDishesPackagePrice(dishesPackage.getPrice());
            Dishes dishes = dishesMapper.selectById(dishesPackage.getDishesId());
            if (dishes != null) {
                vo.setDishesName(dishes.getName());
                vo.setDishesCover(dishes.getCoverUrl());
            }
        }
        return vo;
    }
}
