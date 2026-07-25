package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.entity.DishesTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DishesTableMapper extends BaseMapper<DishesTable> {

    @Update("UPDATE dishes_table SET occupied = 1 WHERE id = #{id} AND status = 1 AND occupied = 0")
    int occupyIfAvailable(@Param("id") Integer id);

    @Update("UPDATE dishes_table SET occupied = 0 WHERE id = #{id} AND occupied = 1")
    int releaseIfOccupied(@Param("id") Integer id);
}
