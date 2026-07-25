package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    @Update("UPDATE orders SET status = #{targetStatus} WHERE id = #{id} AND user_id = #{userId} AND status = #{expectedStatus}")
    int cancelIfPending(@Param("id") Integer id,
                        @Param("userId") Integer userId,
                        @Param("expectedStatus") Integer expectedStatus,
                        @Param("targetStatus") Integer targetStatus);

    @Update("UPDATE orders SET status = #{targetStatus}, serve_food_time = #{serveFoodTime} WHERE id = #{id} AND status = #{expectedStatus}")
    int startServing(@Param("id") Integer id,
                     @Param("expectedStatus") Integer expectedStatus,
                     @Param("targetStatus") Integer targetStatus,
                     @Param("serveFoodTime") LocalDateTime serveFoodTime);

    @Update("UPDATE orders SET status = #{targetStatus} WHERE id = #{id} AND status = #{expectedStatus}")
    int transitionStatus(@Param("id") Integer id,
                         @Param("expectedStatus") Integer expectedStatus,
                         @Param("targetStatus") Integer targetStatus);

    @Update("UPDATE orders SET status = #{targetStatus}, pay_time = #{payTime} WHERE id = #{id} AND user_id = #{userId} AND status = #{expectedStatus}")
    int markPaid(@Param("id") Integer id,
                 @Param("userId") Integer userId,
                 @Param("expectedStatus") Integer expectedStatus,
                 @Param("targetStatus") Integer targetStatus,
                 @Param("payTime") LocalDateTime payTime);

    @Select("""
            SELECT DATE(pay_time) AS date, COALESCE(SUM(total_price), 0) AS value
            FROM orders
            WHERE pay_time >= #{startTime} AND status <> 5
            GROUP BY DATE(pay_time)
            ORDER BY DATE(pay_time)
            """)
    List<Map<String, Object>> selectSalesByDay(@Param("startTime") LocalDateTime startTime);
}
