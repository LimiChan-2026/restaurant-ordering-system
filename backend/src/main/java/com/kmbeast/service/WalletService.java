package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.WalletRechargeDTO;
import com.kmbeast.pojo.dto.WalletQueryDTO;
import com.kmbeast.pojo.entity.Orders;
import com.kmbeast.pojo.entity.Wallet;
import com.kmbeast.pojo.vo.WalletVO;

import java.util.List;

public interface WalletService {

    R<Wallet> detail();

    R<Void> recharge(WalletRechargeDTO rechargeDTO);

    R<Void> payOrder(Orders order);
    R<Void> refundOrder(Orders order);

    R<List<WalletVO>> list(WalletQueryDTO queryDTO);
}
