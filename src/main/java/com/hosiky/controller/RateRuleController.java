package com.hosiky.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.Result;
import com.hosiky.domain.po.RateRule;
import com.hosiky.domain.vo.RateRuleVO;
import com.hosiky.service.IRateRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;

@Tag(name = "费率规则管理")
@RestController
@RequestMapping("/rate-rule")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RateRuleController {

    private final IRateRuleService rateRuleService;

    @Operation(summary = "创建费率规则")
    @PostMapping
    public Result createRateRule(@Valid @RequestBody RateRule rateRule) {
        try {
            boolean success = rateRuleService.createRateRule(rateRule);
            return success ? Result.ok("费率规则创建成功") : Result.errorMsg("费率规则创建失败");
        } catch (IllegalArgumentException e) {
            log.warn("参数验证失败: {}", e.getMessage());
            return Result.errorMsg(e.getMessage());
        } catch (RuntimeException e) {
            log.warn("业务逻辑错误: {}", e.getMessage());
            return Result.errorMsg(e.getMessage());
        } catch (Exception e) {
            log.error("创建费率规则异常", e);
            return Result.errorMsg("系统异常，请稍后重试");
        }
    }

    @Operation(summary = "更新费率规则")
    @PutMapping
    public Result updateRateRule(@Valid @RequestBody RateRule rateRule) {
        try {
            boolean success = rateRuleService.updateRateRule(rateRule);
            return success ? Result.ok("费率规则更新成功") : Result.errorMsg("费率规则更新失败");
        } catch (IllegalArgumentException e) {
            log.warn("参数验证失败: {}", e.getMessage());
            return Result.errorMsg(e.getMessage());
        } catch (RuntimeException e) {
            log.warn("业务逻辑错误: {}", e.getMessage());
            return Result.errorMsg(e.getMessage());
        } catch (Exception e) {
            log.error("更新费率规则异常", e);
            return Result.errorMsg("系统异常，请稍后重试");
        }
    }

    @Operation(summary = "删除费率规则（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result deleteRateRule(@PathVariable Integer id) {
        try {
            boolean success = rateRuleService.removeById(id);
            return success ? Result.ok("费率规则删除成功") : Result.errorMsg("费率规则删除失败");
        } catch (Exception e) {
            log.error("删除费率规则异常: id={}", id, e);
            return Result.errorMsg("删除失败");
        }
    }

    @Operation(summary = "根据ID查询费率规则")
    @GetMapping("/{id}")
    public Result getRateRuleById(@PathVariable Integer id) {
        try {
            RateRuleVO rateRuleVO = rateRuleService.getRateRuleById(id);
            return Result.ok(rateRuleVO);
        } catch (RuntimeException e) {
            log.warn("费率规则不存在: id={}", id);
            return Result.errorMsg(e.getMessage());
        } catch (Exception e) {
            log.error("查询费率规则异常: id={}", id, e);
            return Result.errorMsg("查询失败");
        }
    }

    @Operation(summary = "分页查询费率规则")
    @GetMapping("/page")
    public Result  getRateRulePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer isWeekend) {

        try {
            Page<RateRuleVO> result = rateRuleService.getRateRulePage(page, size, brandId, categoryId, isWeekend);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("分页查询费率规则异常", e);
            return Result.errorMsg("查询失败");
        }
    }

    @Operation(summary = "根据条件查询费率规则")
    @GetMapping("/by-conditions")
    public Result getRateRuleByConditions(
            @RequestParam @NotNull Integer brandId,
            @RequestParam @NotNull Integer categoryId,
            @RequestParam @NotNull Integer isWeekend) {

        try {
            RateRuleVO rateRuleVO = rateRuleService.getRateRuleByConditions(brandId, categoryId, isWeekend);
            return Result.ok(rateRuleVO);
        } catch (RuntimeException e) {
            log.warn("未找到费率规则: brandId={}, categoryId={}, isWeekend={}", brandId, categoryId, isWeekend);
            return Result.errorMsg(e.getMessage());
        } catch (Exception e) {
            log.error("条件查询费率规则异常", e);
            return Result.errorMsg("查询失败");
        }
    }

    @Operation(summary = "根据品牌ID查询所有费率规则")
    @GetMapping("/brand/{brandId}")
    public Result getRateRulesByBrandId(@PathVariable Integer brandId) {
        try {
            List<RateRuleVO> rateRules = rateRuleService.getRateRulesByBrandId(brandId);
            return Result.ok(rateRules);
        } catch (Exception e) {
            log.error("查询品牌费率规则异常: brandId={}", brandId, e);
            return Result.errorMsg("查询失败");
        }
    }

    @Operation(summary = "计算租车最终价格")
    @GetMapping("/calculate-price")
    public Result calculateFinalPrice(
            @RequestParam @NotNull Integer brandId,
            @RequestParam @NotNull Integer categoryId,
            @RequestParam @NotNull Integer isWeekend,
            @RequestParam @NotNull Integer days) {

        try {
            BigDecimal finalPrice = rateRuleService.calculateFinalPrice(brandId, categoryId, isWeekend, days);
            return Result.ok(finalPrice);
        } catch (RuntimeException e) {
            log.warn("价格计算失败: {}", e.getMessage());
            return Result.errorMsg(e.getMessage());
        } catch (Exception e) {
            log.error("计算价格异常", e);
            return Result.errorMsg("计算失败");
        }
    }

    @Operation(summary = "验证费率规则是否重复")
    @GetMapping("/validate-duplicate")
    public Result validateRateRuleDuplicate(
            @RequestParam @NotNull Integer brandId,
            @RequestParam @NotNull Integer categoryId,
            @RequestParam @NotNull Integer isWeekend,
            @RequestParam(required = false) Integer excludeId) {

        try {
            boolean isDuplicate = rateRuleService.validateRateRuleDuplicate(brandId, categoryId, isWeekend, excludeId);
            return Result.ok(isDuplicate);
        } catch (Exception e) {
            log.error("验证重复异常", e);
            return Result.errorMsg("验证失败");
        }
    }
}