package com.hosiky.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.domain.po.RateRule;
import com.hosiky.domain.vo.RateRuleVO;
import com.hosiky.mapper.RateRuleMapper;
import com.hosiky.service.IRateRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RateRuleServiceImpl extends ServiceImpl<RateRuleMapper, RateRule> implements IRateRuleService {

    private RateRuleMapper rateRuleMapper;

    @Override
    public boolean createRateRule(RateRule rateRule) {
        // 参数验证
        Assert.notNull(rateRule, "费率规则不能为空");
        Assert.notNull(rateRule.getBrandId(), "品牌ID不能为空");
        Assert.notNull(rateRule.getCategoryId(), "分类ID不能为空");
        Assert.notNull(rateRule.getIsWeekend(), "日期类型不能为空");
        Assert.notNull(rateRule.getBasePrice(), "基础价格不能为空");
        Assert.notNull(rateRule.getRatio(), "浮动系数不能为空");

        // 验证浮动系数范围
        if (rateRule.getRatio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("浮动系数必须大于0");
        }

        // 检查规则是否重复
        if (validateRateRuleDuplicate(rateRule.getBrandId(), rateRule.getCategoryId(),
                rateRule.getIsWeekend(), null)) {
            throw new RuntimeException("相同品牌、分类和日期类型的费率规则已存在");
        }
        return this.save(rateRule);
    }

    @Override
    public boolean updateRateRule(RateRule rateRule) {
        if (rateRule.getId() == null) {
            throw new IllegalArgumentException("费率规则ID不能为空");
        }

        // 检查记录是否存在
        RateRule existing = this.getById(rateRule.getId());
        if (existing == null || existing.getDeleted() == 1) {
            throw new RuntimeException("费率规则不存在");
        }

        // 验证浮动系数
        if (rateRule.getRatio() != null && rateRule.getRatio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("浮动系数必须大于0");
        }

        // 如果修改了关键字段，检查重复
        if ((rateRule.getBrandId() != null && !rateRule.getBrandId().equals(existing.getBrandId())) ||
                (rateRule.getCategoryId() != null && !rateRule.getCategoryId().equals(existing.getCategoryId())) ||
                (rateRule.getIsWeekend() != null && !rateRule.getIsWeekend().equals(existing.getIsWeekend()))) {

            if (validateRateRuleDuplicate(
                    rateRule.getBrandId() != null ? rateRule.getBrandId() : existing.getBrandId(),
                    rateRule.getCategoryId() != null ? rateRule.getCategoryId() : existing.getCategoryId(),
                    rateRule.getIsWeekend() != null ? rateRule.getIsWeekend() : existing.getIsWeekend(),
                    rateRule.getId())) {
                throw new RuntimeException("相同品牌、分类和日期类型的费率规则已存在");
            }
        }

        return this.updateById(rateRule);
    }

    @Override
    public RateRuleVO getRateRuleById(Integer id) {
        RateRule rateRule = this.getById(id);
        if (rateRule == null || rateRule.getDeleted() == 1) {
            throw new RuntimeException("费率规则不存在");
        }
        return convertToVO(rateRule);
    }

    @Override
    public Page<RateRuleVO> getRateRulePage(Integer page, Integer size, Integer brandId, Integer categoryId, Integer isWeekend) {
        // 参数验证和默认值设置
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 10;

        // 构建查询条件
        LambdaQueryWrapper<RateRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(brandId != null, RateRule::getBrandId, brandId)
                .eq(categoryId != null, RateRule::getCategoryId, categoryId)
                .eq(isWeekend != null, RateRule::getIsWeekend, isWeekend)
                .orderByDesc(RateRule::getUpdatedAt);

        // 执行分页查询
        Page<RateRule> entityPage = new Page<>(page, size);
        Page<RateRule> resultPage = this.page(entityPage, queryWrapper);

        // 转换为VO
        return (Page<RateRuleVO>) resultPage.convert(this::convertToVO);
    }

    @Override
    public RateRuleVO getRateRuleByConditions(Integer brandId, Integer categoryId, Integer isWeekend) {
        Assert.notNull(brandId, "品牌ID不能为空");
        Assert.notNull(categoryId, "分类ID不能为空");
        Assert.notNull(isWeekend, "日期类型不能为空");

        RateRule rateRule = rateRuleMapper.selectByConditions(brandId, categoryId, isWeekend);
        if (rateRule == null) {
            throw new RuntimeException("未找到对应的费率规则");
        }
        return convertToVO(rateRule);
    }

    @Override
    public List<RateRuleVO> getRateRulesByBrandId(Integer brandId) {
        Assert.notNull(brandId, "品牌ID不能为空");

        Assert.notNull(brandId, "品牌ID不能为空");

        // 使用 LambdaQueryWrapper 构建查询条件
        LambdaQueryWrapper<RateRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RateRule::getBrandId, brandId)
                .orderByAsc(RateRule::getCreatedAt); // 可选的排序

        List<RateRule> rateRules = rateRuleMapper.selectList(queryWrapper);

        return rateRules.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }


    @Override
    public boolean validateRateRuleDuplicate(Integer brandId, Integer categoryId, Integer isWeekend, Integer excludeId) {
        Integer count = rateRuleMapper.countDuplicateRules(brandId, categoryId, isWeekend,
                excludeId != null ? excludeId : -1);
        return count != null && count > 0;
    }


    @Override
    public BigDecimal calculateFinalPrice(Integer brandId, Integer categoryId, Integer isWeekend, Integer days) {
        Assert.notNull(days, "租用天数不能为空");
        if (days <= 0) {
            throw new IllegalArgumentException("租用天数必须大于0");
        }

        RateRule rateRule = rateRuleMapper.selectByConditions(brandId, categoryId, isWeekend);
        if (rateRule == null) {
            throw new RuntimeException("未找到对应的费率规则");
        }

        // 计算最终价格：基础价格 × 浮动系数 × 天数
        BigDecimal dailyPrice = rateRule.getBasePrice().multiply(rateRule.getRatio());
        return dailyPrice.multiply(new BigDecimal(days))
                .setScale(2, RoundingMode.HALF_UP); // 保留两位小数，四舍五入
    }

    /**
     * 将Entity转换为VO
     */
    private RateRuleVO convertToVO(RateRule entity) {
        if (entity == null) {
            return null;
        }
        RateRuleVO vo = new RateRuleVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
