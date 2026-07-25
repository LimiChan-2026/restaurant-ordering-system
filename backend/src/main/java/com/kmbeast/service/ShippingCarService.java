package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.ShippingCarQueryDTO;
import com.kmbeast.pojo.dto.ShippingCarSaveDTO;
import com.kmbeast.pojo.dto.ShippingCarUpdateDTO;

public interface ShippingCarService {
    R save(ShippingCarSaveDTO saveDTO);
    R listUser(ShippingCarQueryDTO queryDTO);
    R update(ShippingCarUpdateDTO updateDTO);
    R delete(Integer id);
}
