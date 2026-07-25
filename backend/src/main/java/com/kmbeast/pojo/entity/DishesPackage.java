package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dishes_package")
public class DishesPackage {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 菜品ID
     */
    private Integer dishesId;

    /**
     * 套餐名
     */
    private String name;

    /**
     * 规格
     */
    private String specs;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
