package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.*;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.*;
import com.kmbeast.pojo.entity.*;
import com.kmbeast.pojo.vo.DishesEvaluationsVO;
import com.kmbeast.service.DishesEvaluationsService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DishesEvaluationsServiceImpl implements DishesEvaluationsService {

    private static final Set<Integer> PURCHASED_ORDER_STATUSES = Set.of(4);
    private final DishesEvaluationsMapper dishesEvaluationsMapper;
    private final ImagesMapper imagesMapper;
    private final DishesMapper dishesMapper;
    private final DishesPackageMapper dishesPackageMapper;
    private final OrdersItemMapper ordersItemMapper;
    private final OrdersMapper ordersMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> save(DishesEvaluationsSaveDTO saveDTO) {
        Integer userId = UserContext.getUserId();
        if (userId == null) return R.unauthorized();
        DishesEvaluationContentDTO contentDTO = saveDTO.getDishesEvaluations();
        Dishes dishes = dishesMapper.selectById(contentDTO.getDishesId());
        if (dishes == null) return R.error("菜品不存在");
        if (!hasPurchased(userId, dishes.getId())) return R.error("仅可评价已购买的菜品");

        DishesEvaluations evaluation = new DishesEvaluations();
        evaluation.setDishesId(dishes.getId());
        evaluation.setUserId(userId);
        evaluation.setContent(contentDTO.getContent().trim());
        evaluation.setRatingValue(contentDTO.getRatingValue());
        evaluation.setReplyStatus(false);
        evaluation.setCreateTime(LocalDateTime.now());
        try {
            dishesEvaluationsMapper.insert(evaluation);
        } catch (DuplicateKeyException e) {
            return R.error("该菜品已评价，请勿重复评价");
        }

        List<EvaluationImageDTO> images = Optional.ofNullable(saveDTO.getImagesList()).orElse(List.of());
        for (int index = 0; index < images.size(); index++) {
            EvaluationImageDTO imageDTO = images.get(index);
            Images image = new Images();
            image.setDishesEvaluationsId(evaluation.getId());
            image.setPictureUrl(imageDTO.getPictureUrl());
            image.setNumber(imageDTO.getNumber() == null ? index + 1 : imageDTO.getNumber());
            imagesMapper.insert(image);
        }
        return R.ok("评价成功");
    }

    @Override
    public R list(DishesEvaluationsQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new DishesEvaluationsQueryDTO();
        }
        LambdaQueryWrapper<DishesEvaluations> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getDishesId() != null) wrapper.eq(DishesEvaluations::getDishesId, queryDTO.getDishesId());
        if (queryDTO.getUserId() != null) wrapper.eq(DishesEvaluations::getUserId, queryDTO.getUserId());
        wrapper.orderByDesc(DishesEvaluations::getCreateTime);
        Page<DishesEvaluations> page = dishesEvaluationsMapper.selectPage(PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
        return R.ok(page.getRecords().stream().map(this::toVO).toList(), page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> delete(Integer id) {
        DishesEvaluations evaluation = dishesEvaluationsMapper.selectById(id);
        if (evaluation == null) return R.error("评价不存在");
        if (!UserContext.isAdmin() && !Objects.equals(evaluation.getUserId(), UserContext.getUserId())) return R.forbidden();
        imagesMapper.delete(new LambdaQueryWrapper<Images>().eq(Images::getDishesEvaluationsId, id));
        dishesEvaluationsMapper.deleteById(id);
        return R.ok("删除评价成功");
    }

    @Override
    public R<Void> reply(DishesEvaluationsReplyDTO replyDTO) {
        DishesEvaluations evaluation = dishesEvaluationsMapper.selectById(replyDTO.getId());
        if (evaluation == null) return R.error("评价不存在");
        evaluation.setReplyContent(replyDTO.getReplyContent().trim());
        evaluation.setReplyStatus(true);
        dishesEvaluationsMapper.updateById(evaluation);
        return R.ok("回复评价成功");
    }

    private boolean hasPurchased(Integer userId, Integer dishesId) {
        List<Integer> packageIds = dishesPackageMapper.selectList(new LambdaQueryWrapper<DishesPackage>()
                        .eq(DishesPackage::getDishesId, dishesId)).stream().map(DishesPackage::getId).toList();
        if (packageIds.isEmpty()) return false;
        List<Integer> orderIds = ordersItemMapper.selectList(new LambdaQueryWrapper<OrdersItem>()
                        .in(OrdersItem::getDishesPackageId, packageIds)).stream().map(OrdersItem::getOrdersId).toList();
        if (orderIds.isEmpty()) return false;
        return ordersMapper.selectCount(new LambdaQueryWrapper<Orders>().eq(Orders::getUserId, userId)
                .in(Orders::getId, orderIds).in(Orders::getStatus, PURCHASED_ORDER_STATUSES)) > 0;
    }

    private DishesEvaluationsVO toVO(DishesEvaluations evaluation) {
        DishesEvaluationsVO vo = new DishesEvaluationsVO();
        BeanUtils.copyProperties(evaluation, vo);
        Dishes dishes = dishesMapper.selectById(evaluation.getDishesId());
        if (dishes != null) { vo.setDishesName(dishes.getName()); vo.setDishesCover(dishes.getCoverUrl()); }
        User user = userMapper.selectById(evaluation.getUserId());
        if (user != null) { vo.setUsername(user.getUsername()); vo.setAvatar(user.getAvatar()); }
        vo.setImagesList(imagesMapper.selectList(new LambdaQueryWrapper<Images>()
                .eq(Images::getDishesEvaluationsId, evaluation.getId()).orderByAsc(Images::getNumber)));
        return vo;
    }
}
