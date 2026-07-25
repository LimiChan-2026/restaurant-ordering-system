package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.CollectionMapper;
import com.kmbeast.mapper.DishesMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesIdDTO;
import com.kmbeast.pojo.entity.Collection;
import com.kmbeast.pojo.entity.Dishes;
import com.kmbeast.pojo.vo.CollectionVO;
import com.kmbeast.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionMapper collectionMapper;
    private final DishesMapper dishesMapper;

    @Override
    public R<Void> saveOrCancel(DishesIdDTO dishesIdDTO) {
        Integer userId = UserContext.getUserId();
        if (userId == null) return R.unauthorized();
        if (dishesMapper.selectById(dishesIdDTO.getDishesId()) == null) return R.error("菜品不存在");

        LambdaQueryWrapper<Collection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Collection::getUserId, userId).eq(Collection::getDishesId, dishesIdDTO.getDishesId());
        Collection collection = collectionMapper.selectOne(wrapper, false);
        if (collection != null) {
            collectionMapper.delete(wrapper);
            return R.ok("取消收藏成功");
        }
        Collection newCollection = new Collection();
        newCollection.setUserId(userId);
        newCollection.setDishesId(dishesIdDTO.getDishesId());
        try {
            collectionMapper.insert(newCollection);
        } catch (DuplicateKeyException e) {
            return R.ok("收藏成功");
        }
        return R.ok("收藏成功");
    }

    @Override
    public R<List<CollectionVO>> listUser() {
        Integer userId = UserContext.getUserId();
        if (userId == null) return R.unauthorized();
        List<CollectionVO> result = collectionMapper.selectList(new LambdaQueryWrapper<Collection>()
                        .eq(Collection::getUserId, userId).orderByDesc(Collection::getId))
                .stream().map(this::toVO).filter(item -> item != null).toList();
        return R.ok(result, (long) result.size());
    }

    @Override
    public R<Boolean> isCollected(Integer dishesId) {
        Integer userId = UserContext.getUserId();
        if (userId == null) return R.unauthorized();
        Long count = collectionMapper.selectCount(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId).eq(Collection::getDishesId, dishesId));
        return R.ok(count > 0);
    }

    private CollectionVO toVO(Collection collection) {
        Dishes dishes = dishesMapper.selectById(collection.getDishesId());
        if (dishes == null) return null;
        CollectionVO vo = new CollectionVO();
        vo.setId(collection.getId());
        vo.setDishesId(dishes.getId());
        vo.setDishesName(dishes.getName());
        vo.setDishesDetail(dishes.getDetail());
        vo.setDishesCover(dishes.getCoverUrl());
        vo.setDishesStatus(dishes.getStatus());
        return vo;
    }
}
