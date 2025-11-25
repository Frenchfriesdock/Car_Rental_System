package com.hosiky.controller;

import com.hosiky.common.Result;
import com.hosiky.domain.dto.MerchantDTO;
import com.hosiky.domain.po.Merchant;
import com.hosiky.domain.vo.MerchantVO;
import com.hosiky.service.IMerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Tag(name = "车商入驻接口")
@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
@Slf4j

public class MerchantController {

    private final IMerchantService merchantService;

    @PostMapping("/add")
    @Operation(summary = "添加入驻车商")
    public Result addMerchant(@RequestBody MerchantDTO merchantDTO) {

        return Result.ok(merchantService.addMerchant(merchantDTO));
    }

    /**
     *
     * @param merchantDto
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改入驻车商信息")
    public Result updateMerchant(@RequestBody MerchantDTO merchantDto) {
        return Result.ok(merchantService.updateMerchantDTO(merchantDto));
    }

    @GetMapping("/{merchantId}")
    @Operation(summary = "获取入驻车商信息")
    public Result getMerchant(@PathVariable int merchantId) {

        MerchantVO merchantVO = merchantService.getMerchantById(merchantId);
        return Result.ok(merchantVO);
    }

    @DeleteMapping("/{merchantId}")
    @Operation(summary = "逻辑删除")
    public Result deleteMerchant(@PathVariable int merchantId) {

        return Result.ok(merchantService.deleteMerchant(merchantId));
    }
    

}
