package com.kmbeast.pojo.entity;
import com.baomidou.mybatisplus.annotation.*;import lombok.Data;import java.time.LocalDateTime;
@Data @TableName("orders_refund_reply") public class OrdersRefundReply {@TableId(type=IdType.AUTO)private Integer id;private Integer ordersId;private Integer status;private String refundCause;private String rejectRefundCause;private LocalDateTime refundTime;private LocalDateTime createTime;}
