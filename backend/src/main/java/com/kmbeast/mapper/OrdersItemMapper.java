package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.entity.OrdersItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrdersItemMapper extends BaseMapper<OrdersItem> {

    @Delete("DELETE FROM orders_item WHERE orders_id = #{ordersId}")
    int deleteByOrdersId(@Param("ordersId") Integer ordersId);
}
