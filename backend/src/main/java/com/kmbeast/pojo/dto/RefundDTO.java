package com.kmbeast.pojo.dto;import jakarta.validation.constraints.*;import lombok.Data;@Data public class RefundDTO{@NotNull private Integer ordersId;@NotBlank private String refundCause;}
