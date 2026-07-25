package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.OrdersMapper;
import com.kmbeast.mapper.DishesTableMapper;
import com.kmbeast.mapper.UserMapper;
import com.kmbeast.mapper.WalletInfoMapper;
import com.kmbeast.mapper.WalletMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.WalletRechargeDTO;
import com.kmbeast.pojo.dto.WalletQueryDTO;
import com.kmbeast.pojo.entity.Wallet;
import com.kmbeast.pojo.entity.WalletInfo;
import com.kmbeast.pojo.entity.Orders;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.pojo.vo.WalletVO;
import com.kmbeast.service.WalletService;
import com.kmbeast.service.MessageService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private static final int PRIVATE_WALLET = 1;
    private static final int MERCHANT_WALLET = 2;
    private static final int ADMIN_ROLE = 2;
    private static final int STATUS_PENDING_PAYMENT = 1;
    private static final int STATUS_PAID = 2;
    private static final int STATUS_CANCELLED = 5;

    private final WalletMapper walletMapper;
    private final WalletInfoMapper walletInfoMapper;
    private final OrdersMapper ordersMapper;
    private final DishesTableMapper dishesTableMapper;
    private final UserMapper userMapper;
    private final MessageService messageService;

    @Override
    public R<Wallet> detail() {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }
        return R.ok(findOrCreatePrivateWallet(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> recharge(WalletRechargeDTO rechargeDTO) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }

        Wallet wallet = findOrCreatePrivateWallet(userId);
        if (!Boolean.TRUE.equals(wallet.getStatus())) {
            return R.error("钱包已冻结，无法充值");
        }

        int credited = walletMapper.update(null, new LambdaUpdateWrapper<Wallet>()
                .eq(Wallet::getId, wallet.getId())
                .eq(Wallet::getStatus, true)
                .setSql("surplus = surplus + {0}", rechargeDTO.getMoney()));
        if (credited == 0) {
            return rollback("钱包已冻结或充值失败");
        }
        saveWalletInfo(wallet.getId(), "钱包充值", rechargeDTO.getMoney());
        return R.ok("充值成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> payOrder(Orders order) {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }

        BigDecimal totalPrice = order.getTotalPrice();
        if (totalPrice == null || totalPrice.signum() < 0) {
            return R.error("订单金额异常");
        }

        Wallet userWallet = findOrCreatePrivateWallet(userId);
        if (!Boolean.TRUE.equals(userWallet.getStatus())) {
            return R.error("用户钱包已冻结，无法支付");
        }
        Wallet merchantWallet = findMerchantWallet();
        if (merchantWallet == null) {
            return R.error("商家钱包不存在，无法支付");
        }
        if (!Boolean.TRUE.equals(merchantWallet.getStatus())) {
            return R.error("商家钱包已冻结，暂无法支付");
        }

        LocalDateTime payTime = LocalDateTime.now();
        int updatedOrders = ordersMapper.markPaid(order.getId(), userId,
                STATUS_PENDING_PAYMENT, STATUS_PAID, payTime);
        if (updatedOrders == 0) {
            return R.error(2002, "订单状态异常");
        }

        int deducted = walletMapper.update(null, new LambdaUpdateWrapper<Wallet>()
                .eq(Wallet::getId, userWallet.getId())
                .eq(Wallet::getStatus, true)
                .ge(Wallet::getSurplus, totalPrice)
                .setSql("surplus = surplus - {0}", totalPrice));
        if (deducted == 0) {
            return rollback("钱包余额不足或已冻结，无法支付");
        }

        int credited = walletMapper.update(null, new LambdaUpdateWrapper<Wallet>()
                .eq(Wallet::getId, merchantWallet.getId())
                .eq(Wallet::getStatus, true)
                .setSql("surplus = surplus + {0}", totalPrice));
        if (credited == 0) {
            return rollback("商家钱包入账失败");
        }

        saveWalletInfo(userWallet.getId(), "订单支付 - " + order.getCode(), totalPrice.negate());
        saveWalletInfo(merchantWallet.getId(), "订单收款 - " + order.getCode(), totalPrice);
        messageService.notifyUser(userId, 2, "订单 " + order.getCode() + " 已支付，商家将尽快为您准备餐品");
        return R.ok("支付成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> refundOrder(Orders order) {
        if (order == null || !Integer.valueOf(STATUS_PAID).equals(order.getStatus())) {
            return R.error("订单状态不支持退款");
        }
        if (order.getTotalPrice() == null || order.getTotalPrice().signum() < 0) {
            return R.error("订单金额异常");
        }
        Wallet userWallet = findOrCreatePrivateWallet(order.getUserId());
        Wallet merchantWallet = findMerchantWallet();
        if (merchantWallet == null) {
            return R.error("商家钱包不存在，无法退款");
        }
        int updatedOrders = ordersMapper.transitionStatus(order.getId(), STATUS_PAID, STATUS_CANCELLED);
        if (updatedOrders == 0) {
            return rollback("订单状态已变化，无法退款");
        }
        if (order.getDishesTableId() == null || dishesTableMapper.releaseIfOccupied(order.getDishesTableId()) == 0) {
            return rollback("餐桌占用状态异常，无法退款");
        }
        int deducted = walletMapper.update(null, new LambdaUpdateWrapper<Wallet>()
                .eq(Wallet::getId, merchantWallet.getId())
                .eq(Wallet::getStatus, true)
                .ge(Wallet::getSurplus, order.getTotalPrice())
                .setSql("surplus = surplus - {0}", order.getTotalPrice()));
        if (deducted == 0) {
            return rollback("商家余额不足或钱包已冻结，无法退款");
        }
        int credited = walletMapper.update(null, new LambdaUpdateWrapper<Wallet>()
                .eq(Wallet::getId, userWallet.getId())
                .eq(Wallet::getStatus, true)
                .setSql("surplus = surplus + {0}", order.getTotalPrice()));
        if (credited == 0) {
            return rollback("用户钱包已冻结，退款入账失败");
        }
        saveWalletInfo(userWallet.getId(), "订单退款 - " + order.getCode(), order.getTotalPrice());
        saveWalletInfo(merchantWallet.getId(), "订单退款 - " + order.getCode(), order.getTotalPrice().negate());
        return R.ok("退款成功");
    }

    @Override
    public R<List<WalletVO>> list(WalletQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new WalletQueryDTO();
        }
        LambdaQueryWrapper<Wallet> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getType() != null) {
            wrapper.eq(Wallet::getType, queryDTO.getType());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Wallet::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getUsername())) {
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.like(User::getUsername, queryDTO.getUsername());
            List<Integer> userIds = userMapper.selectList(userWrapper).stream().map(User::getId).toList();
            if (userIds.isEmpty()) {
                return R.ok(List.of(), 0L);
            }
            wrapper.in(Wallet::getUserId, userIds);
        }
        wrapper.orderByDesc(Wallet::getCreateTime);
        Page<Wallet> page = walletMapper.selectPage(PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
        List<WalletVO> wallets = page.getRecords().stream().map(this::toWalletVO).toList();
        return R.ok(wallets, page.getTotal());
    }

    private Wallet findOrCreatePrivateWallet(Integer userId) {
        LambdaQueryWrapper<Wallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Wallet::getUserId, userId)
                .eq(Wallet::getType, PRIVATE_WALLET)
                .orderByAsc(Wallet::getId);
        List<Wallet> wallets = walletMapper.selectList(wrapper);
        if (!wallets.isEmpty()) {
            return wallets.get(0);
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setSurplus(BigDecimal.ZERO);
        wallet.setStatus(true);
        wallet.setType(PRIVATE_WALLET);
        wallet.setCreateTime(LocalDateTime.now());
        try {
            walletMapper.insert(wallet);
            return wallet;
        } catch (DuplicateKeyException e) {
            return walletMapper.selectOne(new LambdaQueryWrapper<Wallet>()
                    .eq(Wallet::getUserId, userId)
                    .eq(Wallet::getType, PRIVATE_WALLET));
        }
    }

    private Wallet findMerchantWallet() {
        LambdaQueryWrapper<Wallet> walletWrapper = new LambdaQueryWrapper<>();
        walletWrapper.eq(Wallet::getType, MERCHANT_WALLET).orderByAsc(Wallet::getId);
        List<Wallet> merchantWallets = walletMapper.selectList(walletWrapper);
        if (!merchantWallets.isEmpty()) {
            return merchantWallets.get(0);
        }

        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getRole, ADMIN_ROLE).orderByAsc(User::getId);
        List<User> admins = userMapper.selectList(userWrapper);
        if (admins.isEmpty()) {
            return null;
        }

        Wallet merchantWallet = new Wallet();
        merchantWallet.setUserId(admins.get(0).getId());
        merchantWallet.setSurplus(BigDecimal.ZERO);
        merchantWallet.setStatus(true);
        merchantWallet.setType(MERCHANT_WALLET);
        merchantWallet.setCreateTime(LocalDateTime.now());
        try {
            walletMapper.insert(merchantWallet);
            return merchantWallet;
        } catch (DuplicateKeyException e) {
            return walletMapper.selectOne(new LambdaQueryWrapper<Wallet>()
                    .eq(Wallet::getUserId, admins.get(0).getId())
                    .eq(Wallet::getType, MERCHANT_WALLET));
        }
    }

    private WalletVO toWalletVO(Wallet wallet) {
        WalletVO vo = new WalletVO();
        vo.setId(wallet.getId());
        vo.setUserId(wallet.getUserId());
        vo.setSurplus(wallet.getSurplus());
        vo.setStatus(wallet.getStatus());
        vo.setType(wallet.getType());
        vo.setCreateTime(wallet.getCreateTime());
        User user = userMapper.selectById(wallet.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }

    private void saveWalletInfo(Integer walletId, String detail, BigDecimal money) {
        WalletInfo walletInfo = new WalletInfo();
        walletInfo.setWalletId(walletId);
        walletInfo.setDetail(detail);
        walletInfo.setSurplusMoney(money);
        walletInfo.setCreateTime(LocalDateTime.now());
        walletInfoMapper.insert(walletInfo);
    }

    private R<Void> rollback(String message) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return R.error(message);
    }
}
