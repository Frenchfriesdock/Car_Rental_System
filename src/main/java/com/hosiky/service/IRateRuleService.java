package com.hosiky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.domain.po.RateRule;
import com.hosiky.domain.vo.RateRuleVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public interface IRateRuleService extends IService<RateRule> {

    boolean createRateRule(@Valid RateRule rateRule);

    boolean updateRateRule(@Valid RateRule rateRule);

    RateRuleVO getRateRuleById(Integer id);

    Page<RateRuleVO> getRateRulePage(Integer page, Integer size, Integer brandId, Integer categoryId, Integer isWeekend);

    RateRuleVO getRateRuleByConditions(@NotNull Integer brandId, @NotNull Integer categoryId, @NotNull Integer isWeekend);

    List<RateRuleVO> getRateRulesByBrandId(Integer brandId);

    BigDecimal calculateFinalPrice(@NotNull Integer brandId, @NotNull Integer categoryId, @NotNull Integer isWeekend, @NotNull Integer days);

    boolean validateRateRuleDuplicate(@NotNull Integer brandId, @NotNull Integer categoryId, @NotNull Integer isWeekend, Integer excludeId);
}
