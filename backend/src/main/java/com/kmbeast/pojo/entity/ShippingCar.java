package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("shipping_car")
public class ShippingCar {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer dishesPackageId;
    private Integer plusNumber;

    /** 数据库字段沿用既有表结构中的拼写。 */
    @TableField("is_seleced")
    private Boolean isSelected;
    private LocalDateTime createTime;
}
