package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private Integer userId;
    private Integer dishesTableId;
    private Integer status;
    private BigDecimal totalPrice;
    private LocalDateTime serveFoodTime;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}
