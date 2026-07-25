package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.entity.ShippingCar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface ShippingCarMapper extends BaseMapper<ShippingCar> {

    @Insert("""
            INSERT INTO shipping_car(user_id, dishes_package_id, plus_number, is_seleced, create_time)
            VALUES(#{userId}, #{dishesPackageId}, #{plusNumber}, 0, #{createTime})
            ON DUPLICATE KEY UPDATE plus_number = plus_number + VALUES(plus_number)
            """)
    int addOrIncrement(@Param("userId") Integer userId,
                       @Param("dishesPackageId") Integer dishesPackageId,
                       @Param("plusNumber") Integer plusNumber,
                       @Param("createTime") LocalDateTime createTime);
}
