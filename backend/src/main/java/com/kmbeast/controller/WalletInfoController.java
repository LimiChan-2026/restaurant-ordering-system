package com.kmbeast.controller;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.WalletInfoQueryDTO;
import com.kmbeast.pojo.entity.WalletInfo;
import com.kmbeast.service.WalletInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wallet-info")
@RequiredArgsConstructor
public class WalletInfoController {

    private final WalletInfoService walletInfoService;

    @PostMapping("/list")
    public R<List<WalletInfo>> list(@Valid @RequestBody WalletInfoQueryDTO queryDTO) {
        return walletInfoService.list(queryDTO);
    }
}
