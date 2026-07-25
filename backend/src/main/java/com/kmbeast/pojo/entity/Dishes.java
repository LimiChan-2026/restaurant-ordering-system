package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dishes")
public class Dishes {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 菜品种类ID
     */
    private Integer typeId;

    /**
     * 菜品名
     */
    private String name;

    /**
     * 菜品介绍
     */
    private String detail;

    /**
     * 菜品封面URL
     */
    private String coverUrl;

    /**
     * 状态：0下架，1上架
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
