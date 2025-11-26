package com.hosiky.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.domain.dto.RateRuleDTO;
import com.hosiky.domain.dto.SelectRateRuleConditionsDTO;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RateRuleServiceImpl extends ServiceImpl<RateRuleMapper, RateRule> implements IRateRuleService {

    private final RateRuleMapper rateRuleMapper;

    @Override
    public boolean createRateRule(RateRuleDTO rateRuleDto) {
        // 参数验证
        Assert.notNull(rateRuleDto, "费率规则不能为空");
        Assert.notNull(rateRuleDto.getBrandId(), "品牌ID不能为空");
        Assert.notNull(rateRuleDto.getCategoryId(), "分类ID不能为空");
        Assert.notNull(rateRuleDto.getIsWeekend(), "日期类型不能为空");
        Assert.notNull(rateRuleDto.getBasePrice(), "基础价格不能为空");
        Assert.notNull(rateRuleDto.getRatio(), "浮动系数不能为空");

        // 验证浮动系数范围
        if (rateRuleDto.getRatio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("浮动系数必须大于0");
        }

        // 检查规则是否重复
        if(validateRateRuleDuplicate(rateRuleDto.getBrandId(), rateRuleDto.getCategoryId(),
                rateRuleDto.getIsWeekend())) {
            throw new RuntimeException("相同品牌、分类和日期类型的费率规则已存在");
        }

        RateRule rateRule = new RateRule();
        BeanUtils.copyProperties(rateRuleDto, rateRule);
        rateRule.setCreatedAt(LocalDateTime.now());
        rateRule.setUpdatedAt(LocalDateTime.now());
        return this.save(rateRule);
    }

    public boolean validateRateRuleDuplicate(Integer brandId, Integer categoryId, Integer isWeekend) {
        LambdaQueryWrapper<RateRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RateRule::getBrandId, brandId);
        wrapper.eq(RateRule::getCategoryId, categoryId);
        wrapper.eq(RateRule::getIsWeekend, isWeekend);
        return this.update(wrapper);
    }

    @Override
    public boolean updateRateRule(RateRuleDTO rateRuleDto) {
        // 1. 参数基本校验
        if (rateRuleDto.getId() == null) {
            throw new IllegalArgumentException("费率规则ID不能为空");
        }
        if (rateRuleDto.getBasePrice() != null && rateRuleDto.getBasePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("起步价不能为负数");
        }
        if (rateRuleDto.getRatio() != null && (rateRuleDto.getRatio().compareTo(BigDecimal.ZERO) <= 0 || rateRuleDto.getRatio().compareTo(new BigDecimal("10")) > 0)) {
            throw new IllegalArgumentException("浮动系数必须大于0且小于等于10");
        }

        // 2. 检查待更新的记录是否存在
        RateRule existingRule = this.getById(rateRuleDto.getId());
        if (existingRule == null || existingRule.getDeleted() == 1) {
            throw new RuntimeException("费率规则不存在，更新失败");
        }

        // 3. 使用 UpdateWrapper 进行更新，避免唯一约束冲突
        LambdaUpdateWrapper<RateRule> updateWrapper = createUpdateWrapper(rateRuleDto);
        boolean isSuccess = this.update(updateWrapper);

        if (!isSuccess) {
            // 更新失败可能是由于唯一约束冲突
            throw new RuntimeException("更新失败，原因：数据冲突或记录不存在");
        }
        return true;
    }

    private LambdaUpdateWrapper<RateRule> createUpdateWrapper(RateRuleDTO rateRuleDto) {
        LambdaUpdateWrapper<RateRule> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(RateRule::getId, rateRuleDto.getId()); // 更新条件：ID匹配

        // 动态设置要更新的字段（即使为null也设置）
        updateWrapper.set(RateRule::getBasePrice, rateRuleDto.getBasePrice())
                .eq(rateRuleDto.getBrandId() != null, RateRule::getBrandId, rateRuleDto.getBrandId())
                .eq(rateRuleDto.getCategoryId() != null, RateRule::getCategoryId, rateRuleDto.getCategoryId())
                .eq(rateRuleDto.getIsWeekend() != null, RateRule::getIsWeekend, rateRuleDto.getIsWeekend())
                .set(RateRule::getRatio, rateRuleDto.getRatio())
                .set(RateRule::getUpdatedAt, LocalDateTime.now());

        return updateWrapper;
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
    public List<RateRuleVO> getRateRuleByConditions(SelectRateRuleConditionsDTO selectRateRuleConditionsDTO) {

        LambdaQueryWrapper<RateRule> queryWrapper = new LambdaQueryWrapper<>();

        // 仅为非 null 的参数添加查询条件
        queryWrapper
                .eq(selectRateRuleConditionsDTO.getBrandId() != null, RateRule::getBrandId, selectRateRuleConditionsDTO.getBrandId())
                .eq(selectRateRuleConditionsDTO.getCategoryId() != null, RateRule::getCategoryId, selectRateRuleConditionsDTO.getCategoryId())
                .eq(selectRateRuleConditionsDTO.getIsWeekend() != null, RateRule::getIsWeekend, selectRateRuleConditionsDTO.getIsWeekend())
                .orderByDesc(RateRule::getCreatedAt);

        List<RateRule> rateRules = rateRuleMapper.selectList(queryWrapper);

        return rateRules.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
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
