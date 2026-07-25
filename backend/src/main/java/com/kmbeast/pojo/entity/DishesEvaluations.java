package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dishes_evaluations")
public class DishesEvaluations {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer dishesId;
    private Integer userId;
    private String content;
    private Integer ratingValue;
    private String replyContent;
    private Boolean replyStatus;
    private LocalDateTime createTime;
}
