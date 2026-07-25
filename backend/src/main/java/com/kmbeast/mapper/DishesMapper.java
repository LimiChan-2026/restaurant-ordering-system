package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.entity.Dishes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishesMapper extends BaseMapper<Dishes> {

    @Select("""
            SELECT t.name AS name, COUNT(d.id) AS value
            FROM dishes_type t
            LEFT JOIN dishes d ON d.type_id = t.id
            GROUP BY t.id, t.name
            ORDER BY t.id
            """)
    List<Map<String, Object>> selectTypeCounts();
}
