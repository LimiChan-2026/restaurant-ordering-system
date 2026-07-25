package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesIdDTO;
import com.kmbeast.pojo.vo.CollectionVO;

import java.util.List;

public interface CollectionService {
    R<Void> saveOrCancel(DishesIdDTO dishesIdDTO);
    R<List<CollectionVO>> listUser();
    R<Boolean> isCollected(Integer dishesId);
}
