package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.entity.OrdersRefundReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OrdersRefundReplyMapper extends BaseMapper<OrdersRefundReply> {

    @Update("UPDATE orders_refund_reply SET status = #{processingStatus} WHERE id = #{id} AND status = #{pendingStatus}")
    int claimPending(@Param("id") Integer id,
                     @Param("pendingStatus") Integer pendingStatus,
                     @Param("processingStatus") Integer processingStatus);

    @Update("UPDATE orders_refund_reply SET status = #{approvedStatus}, refund_time = #{refundTime} WHERE id = #{id} AND status = #{processingStatus}")
    int finalizeApproved(@Param("id") Integer id,
                         @Param("processingStatus") Integer processingStatus,
                         @Param("approvedStatus") Integer approvedStatus,
                         @Param("refundTime") LocalDateTime refundTime);

    @Update("UPDATE orders_refund_reply SET status = #{rejectedStatus}, reject_refund_cause = #{cause} WHERE id = #{id} AND status = #{processingStatus}")
    int finalizeRejected(@Param("id") Integer id,
                         @Param("processingStatus") Integer processingStatus,
                         @Param("rejectedStatus") Integer rejectedStatus,
                         @Param("cause") String cause);
}
