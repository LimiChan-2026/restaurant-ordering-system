package com.kmbeast.pojo.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data public class MessageSaveDTO { @NotNull private Integer userId; @NotBlank private String content; }
