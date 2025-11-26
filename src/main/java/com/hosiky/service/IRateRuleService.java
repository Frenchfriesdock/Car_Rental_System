package com.hosiky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.domain.dto.RateRuleDTO;
import com.hosiky.domain.dto.SelectRateRuleConditionsDTO;
import com.hosiky.domain.po.RateRule;
import com.hosiky.domain.vo.RateRuleVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public interface IRateRuleService extends IService<RateRule> {

    boolean createRateRule(@Valid RateRuleDTO rateRuleDto);

    boolean updateRateRule(@Valid RateRuleDTO rateRuleDto);

    RateRuleVO getRateRuleById(Integer id);

    Page<RateRuleVO> getRateRulePage(Integer page, Integer size, Integer brandId, Integer categoryId, Integer isWeekend);

    List<RateRuleVO> getRateRuleByConditions(SelectRateRuleConditionsDTO selectRateRuleConditionsDTO);

    List<RateRuleVO> getRateRulesByBrandId(Integer brandId);

    BigDecimal calculateFinalPrice(@NotNull Integer brandId, @NotNull Integer categoryId, @NotNull Integer isWeekend, @NotNull Integer days);

    boolean validateRateRuleDuplicate(@NotNull Integer brandId, @NotNull Integer categoryId, @NotNull Integer isWeekend);
}
