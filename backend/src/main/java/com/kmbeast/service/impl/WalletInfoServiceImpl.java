package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.WalletInfoMapper;
import com.kmbeast.mapper.WalletMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.WalletInfoQueryDTO;
import com.kmbeast.pojo.entity.Wallet;
import com.kmbeast.pojo.entity.WalletInfo;
import com.kmbeast.service.WalletInfoService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletInfoServiceImpl implements WalletInfoService {

    private static final int PRIVATE_WALLET = 1;

    private final WalletMapper walletMapper;
    private final WalletInfoMapper walletInfoMapper;

    @Override
    public R<List<WalletInfo>> list(WalletInfoQueryDTO queryDTO) {
        if (queryDTO == null || queryDTO.getWalletId() == null) {
            return R.error("钱包ID不能为空");
        }
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }

        Wallet wallet = walletMapper.selectById(queryDTO.getWalletId());
        if (wallet == null || (!UserContext.isAdmin() && (!userId.equals(wallet.getUserId()) || !Integer.valueOf(PRIVATE_WALLET).equals(wallet.getType())))) {
            return R.forbidden();
        }

        LambdaQueryWrapper<WalletInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletInfo::getWalletId, wallet.getId())
                .orderByDesc(WalletInfo::getCreateTime);
        Page<WalletInfo> page = walletInfoMapper.selectPage(PageUtils.of(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
        return R.ok(page.getRecords(), page.getTotal());
    }
}
