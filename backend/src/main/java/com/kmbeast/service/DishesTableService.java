package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesTableQueryDTO;
import com.kmbeast.pojo.dto.DishesTableSaveDTO;
import com.kmbeast.pojo.dto.DishesTableUpdateDTO;

public interface DishesTableService {
    R list(DishesTableQueryDTO queryDTO);

    R<Void> save(DishesTableSaveDTO saveDTO);

    R<Void> update(DishesTableUpdateDTO updateDTO);

    R<Void> delete(Integer id);
}
