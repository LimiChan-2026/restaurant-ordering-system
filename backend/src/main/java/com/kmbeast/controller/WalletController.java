package com.kmbeast.controller;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.WalletRechargeDTO;
import com.kmbeast.pojo.dto.WalletQueryDTO;
import com.kmbeast.pojo.entity.Wallet;
import com.kmbeast.pojo.vo.WalletVO;
import com.kmbeast.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/detail")
    public R<Wallet> detail() {
        return walletService.detail();
    }

    @PostMapping("/recharge")
    public R<Void> recharge(@Valid @RequestBody WalletRechargeDTO rechargeDTO) {
        return walletService.recharge(rechargeDTO);
    }

    @AdminOnly
    @PostMapping("/list")
    public R<List<WalletVO>> list(@RequestBody(required = false) WalletQueryDTO queryDTO) {
        return walletService.list(queryDTO == null ? new WalletQueryDTO() : queryDTO);
    }
}
