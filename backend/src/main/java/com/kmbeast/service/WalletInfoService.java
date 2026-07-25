package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.WalletInfoQueryDTO;
import com.kmbeast.pojo.entity.WalletInfo;

import java.util.List;

public interface WalletInfoService {

    R<List<WalletInfo>> list(WalletInfoQueryDTO queryDTO);
}
