package com.kmbeast.pojo.vo;

import com.kmbeast.pojo.entity.Images;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DishesEvaluationsVO {
    private Integer id;
    private Integer dishesId;
    private Integer userId;
    private String content;
    private Integer ratingValue;
    private String replyContent;
    private Boolean replyStatus;
    private LocalDateTime createTime;
    private String dishesName;
    private String dishesCover;
    private String username;
    private String avatar;
    private List<Images> imagesList;
}
