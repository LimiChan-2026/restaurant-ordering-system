package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("wallet_info")
public class WalletInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer walletId;
    private String detail;
    private BigDecimal surplusMoney;
    private LocalDateTime createTime;
}
