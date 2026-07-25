package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("images")
public class Images {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer dishesEvaluationsId;
    private String pictureUrl;
    private Integer number;
}
