package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.OrdersMapper;
import com.kmbeast.mapper.OrdersRefundReplyMapper;
import com.kmbeast.mapper.UserMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.RefundAuditDTO;
import com.kmbeast.pojo.dto.RefundDTO;
import com.kmbeast.pojo.entity.Orders;
import com.kmbeast.pojo.entity.OrdersRefundReply;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.service.MessageService;
import com.kmbeast.service.RefundService;
import com.kmbeast.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private static final int ORDER_STATUS_PAID = 2;
    private static final int REFUND_PENDING = 1;
    private static final int REFUND_APPROVED = 2;
    private static final int REFUND_REJECTED = 3;
    /** 审核过程中的内部临时状态，事务提交前会转换为最终状态。 */
    private static final int REFUND_PROCESSING = 4;
    private static final int ADMIN_ROLE = 2;

    private final OrdersMapper ordersMapper;
    private final OrdersRefundReplyMapper mapper;
    private final WalletService walletService;
    private final MessageService messageService;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> refund(RefundDTO dto) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }

        Orders order = ordersMapper.selectById(dto.getOrdersId());
        if (order == null || !userId.equals(order.getUserId()) || !Integer.valueOf(ORDER_STATUS_PAID).equals(order.getStatus())) {
            return R.error("仅已支付订单可申请退款");
        }

        Long activeCount = mapper.selectCount(new LambdaQueryWrapper<OrdersRefundReply>()
                .eq(OrdersRefundReply::getOrdersId, order.getId()));
        if (activeCount > 0) {
            return R.error("该订单已有退款申请");
        }

        OrdersRefundReply refund = new OrdersRefundReply();
        refund.setOrdersId(order.getId());
        refund.setStatus(REFUND_PENDING);
        refund.setRefundCause(dto.getRefundCause().trim());
        refund.setCreateTime(LocalDateTime.now());
        try {
            mapper.insert(refund);
        } catch (DuplicateKeyException e) {
            return R.error("该订单已有退款申请，请勿重复提交");
        }
        User applicant = userMapper.selectById(userId);
        String applicantName = applicant == null ? "用户" + userId : applicant.getUsername();
        userMapper.selectList(new LambdaQueryWrapper<User>().eq(User::getRole, ADMIN_ROLE))
                .forEach(admin -> messageService.notifyUser(admin.getId(), 1,
                        "用户“" + applicantName + "”为订单 " + order.getCode() + " 提交了退款申请，请及时审核"));
        return R.ok("退款申请已提交");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> audit(RefundAuditDTO dto) {
        if (dto.getStatus() == REFUND_REJECTED
                && (dto.getRejectRefundCause() == null || dto.getRejectRefundCause().isBlank())) {
            return R.error("请填写拒绝原因");
        }

        int claimed = mapper.claimPending(dto.getId(), REFUND_PENDING, REFUND_PROCESSING);
        if (claimed == 0) {
            return R.error("退款申请不存在或已处理");
        }

        OrdersRefundReply refund = mapper.selectById(dto.getId());
        if (refund == null) {
            throw new IllegalStateException("退款申请在审核过程中不存在");
        }
        Orders order = ordersMapper.selectById(refund.getOrdersId());
        if (order == null) {
            throw new IllegalStateException("退款申请关联的订单不存在");
        }

        if (dto.getStatus() == REFUND_APPROVED) {
            R<Void> result = walletService.refundOrder(order);
            if (result.getCode() != 200) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return result;
            }
            refund.setStatus(REFUND_APPROVED);
            refund.setRefundTime(LocalDateTime.now());
            messageService.notifyUser(order.getUserId(), 2, "订单 " + order.getCode() + " 的退款申请已通过");
        } else {
            refund.setStatus(REFUND_REJECTED);
            refund.setRejectRefundCause(dto.getRejectRefundCause().trim());
            messageService.notifyUser(order.getUserId(), 2,
                    "订单 " + order.getCode() + " 的退款申请被拒绝：" + refund.getRejectRefundCause());
        }

        int finalized = refund.getStatus() == REFUND_APPROVED
                ? mapper.finalizeApproved(refund.getId(), REFUND_PROCESSING,
                        REFUND_APPROVED, refund.getRefundTime())
                : mapper.finalizeRejected(refund.getId(), REFUND_PROCESSING,
                        REFUND_REJECTED, refund.getRejectRefundCause());
        if (finalized == 0) {
            throw new IllegalStateException("退款审核状态更新失败");
        }
        return R.ok("审核完成");
    }

    @Override
    public R<List<OrdersRefundReply>> list(Integer ordersId) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }
        if (UserContext.isAdmin()) {
            return R.ok(mapper.selectList(new LambdaQueryWrapper<OrdersRefundReply>()
                    .eq(ordersId != null, OrdersRefundReply::getOrdersId, ordersId)
                    .ne(OrdersRefundReply::getStatus, REFUND_PROCESSING)
                    .orderByDesc(OrdersRefundReply::getCreateTime)));
        }
        if (ordersId != null) {
            Orders order = ordersMapper.selectById(ordersId);
            if (order == null || !userId.equals(order.getUserId())) {
                return R.forbidden();
            }
            return R.ok(mapper.selectList(new LambdaQueryWrapper<OrdersRefundReply>()
                    .eq(OrdersRefundReply::getOrdersId, ordersId)
                    .ne(OrdersRefundReply::getStatus, REFUND_PROCESSING)
                    .orderByDesc(OrdersRefundReply::getCreateTime)));
        }
        List<Integer> orderIds = ordersMapper.selectList(new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getUserId, userId))
                .stream().map(Orders::getId).toList();
        return R.ok(orderIds.isEmpty() ? List.of() : mapper.selectList(new LambdaQueryWrapper<OrdersRefundReply>()
                .in(OrdersRefundReply::getOrdersId, orderIds)
                .ne(OrdersRefundReply::getStatus, REFUND_PROCESSING)
                .orderByDesc(OrdersRefundReply::getCreateTime)));
    }
}
