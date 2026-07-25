package com.kmbeast.pojo.vo;
import lombok.Data;
import java.time.LocalDateTime;
@Data public class MessageVO { private Integer id; private Integer userId; private String username; private String content; private Integer type; private Boolean readStatus; private LocalDateTime createTime; }
