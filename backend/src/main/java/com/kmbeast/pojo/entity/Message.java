package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data @TableName("messages")
public class Message {
    @TableId(type = IdType.AUTO) private Integer id;
    private Integer userId;
    private String content;
    private Integer type;
    private Boolean readStatus;
    private LocalDateTime createTime;
}
