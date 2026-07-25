package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("dishes_type")
public class DishesType {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 图标URL路径
     */
    private String iconUrl;

    /**
     * 菜品种类名
     */
    private String name;
}
