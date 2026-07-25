package com.kmbeast.service;

import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.OrdersMapper;
import com.kmbeast.mapper.OrdersRefundReplyMapper;
import com.kmbeast.mapper.UserMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.RefundDTO;
import com.kmbeast.pojo.dto.RefundAuditDTO;
import com.kmbeast.pojo.entity.OrdersRefundReply;
import com.kmbeast.pojo.entity.Orders;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.service.impl.RefundServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RefundServiceImplTest {
    @Mock private OrdersMapper ordersMapper;
    @Mock private OrdersRefundReplyMapper refundMapper;
    @Mock private WalletService walletService;
    @Mock private MessageService messageService;
    @Mock private UserMapper userMapper;
    @InjectMocks private RefundServiceImpl refundService;

    @AfterEach void clearContext() { UserContext.clear(); }

    @Test void rejectsRefundForAnUnpaidOrder() {
        UserContext.setUserId(8);
        Orders order = new Orders(); order.setId(3); order.setUserId(8); order.setStatus(1);
        when(ordersMapper.selectById(3)).thenReturn(order);
        RefundDTO dto = new RefundDTO(); dto.setOrdersId(3); dto.setRefundCause("不需要了");
        R<Void> result = refundService.refund(dto);
        assertEquals(500, result.getCode());
        verify(refundMapper, never()).insert(any());
    }

    @Test void rejectsRefundForAnotherUsersOrder() {
        UserContext.setUserId(8);
        Orders order = new Orders(); order.setId(3); order.setUserId(9); order.setStatus(2);
        when(ordersMapper.selectById(3)).thenReturn(order);
        RefundDTO dto = new RefundDTO(); dto.setOrdersId(3); dto.setRefundCause("重复下单");
        R<Void> result = refundService.refund(dto);
        assertEquals(500, result.getCode());
        verify(refundMapper, never()).insert(any());
    }

    @Test void auditRejectsAlreadyClaimedRefund() {
        when(refundMapper.claimPending(4, 1, 4)).thenReturn(0);
        RefundAuditDTO dto = new RefundAuditDTO(); dto.setId(4); dto.setStatus(2);

        R<Void> result = refundService.audit(dto);

        assertEquals(500, result.getCode());
        verify(walletService, never()).refundOrder(any());
    }

    @Test void refundNotifiesEachAdministrator() {
        UserContext.setUserId(8);
        Orders order = new Orders(); order.setId(3); order.setUserId(8); order.setStatus(2); order.setCode("ORDER-3");
        User applicant = new User(); applicant.setId(8); applicant.setUsername("测试用户");
        User admin = new User(); admin.setId(1); admin.setRole(2);
        when(ordersMapper.selectById(3)).thenReturn(order);
        when(userMapper.selectById(8)).thenReturn(applicant);
        when(userMapper.selectList(any())).thenReturn(List.of(admin));
        RefundDTO dto = new RefundDTO(); dto.setOrdersId(3); dto.setRefundCause("菜品不符合预期");

        R<Void> result = refundService.refund(dto);

        assertEquals(200, result.getCode());
        verify(messageService).notifyUser(eq(1), eq(1), contains("ORDER-3"));
    }

    @Test void auditRejectsMissingOrderWithoutCallingWallet() {
        when(refundMapper.claimPending(4, 1, 4)).thenReturn(1);
        OrdersRefundReply refund = new OrdersRefundReply(); refund.setId(4); refund.setOrdersId(99); refund.setStatus(4);
        when(refundMapper.selectById(4)).thenReturn(refund);
        when(ordersMapper.selectById(99)).thenReturn(null);
        RefundAuditDTO dto = new RefundAuditDTO(); dto.setId(4); dto.setStatus(2);

        org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class, () -> refundService.audit(dto));
        verify(walletService, never()).refundOrder(any());
    }
}
