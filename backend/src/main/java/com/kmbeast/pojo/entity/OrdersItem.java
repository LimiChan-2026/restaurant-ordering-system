package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders_item")
public class OrdersItem {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer ordersId;
    private Integer dishesPackageId;
    private BigDecimal snapPrice;
    private String snapName;
    private String snapCover;
    private Integer buyNumber;
    private BigDecimal totalPrice;
    private LocalDateTime createTime;
}
