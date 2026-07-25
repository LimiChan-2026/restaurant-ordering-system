package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wallet")
public class Wallet {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private BigDecimal surplus;
    private Boolean status;
    private Integer type;
    private LocalDateTime createTime;
}
