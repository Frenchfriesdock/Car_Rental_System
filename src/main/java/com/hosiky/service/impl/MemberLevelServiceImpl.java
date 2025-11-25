package com.hosiky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.domain.po.MemberLevel;
import com.hosiky.domain.dto.MemberLevelDTO;
import com.hosiky.domain.vo.MemberLevelVO;
import com.hosiky.mapper.MemberLevelMapper;
import com.hosiky.service.IMemberLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Period.between;

@Service
@Slf4j
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelMapper, MemberLevel>
        implements IMemberLevelService {


    @Override
    public Page<MemberLevelVO> getMemberLevelPage(Integer page, Integer size, String levelName) {
        Page<MemberLevel> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<MemberLevel> queryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        queryWrapper.like(StringUtils.hasText(levelName), MemberLevel::getLevelName, levelName)
                .orderByAsc(MemberLevel::getMinValue); // 按成长值下限升序排列

        Page<MemberLevel> memberLevelPage = this.page(pageInfo, queryWrapper);

        // 转换为VO
        Page<MemberLevelVO> voPage = new Page<>(page, size, memberLevelPage.getTotal());
        List<MemberLevelVO> voList = memberLevelPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public MemberLevelVO getMemberLevelById(Integer id) {
        MemberLevel memberLevel = this.getById(id);
        if (memberLevel == null) {
            throw new RuntimeException("会员等级不存在");
        }
        return convertToVO(memberLevel);
    }

    @Override
    public List<MemberLevelVO> getActiveMemberLevels() {
        LambdaQueryWrapper<MemberLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(MemberLevel::getMinValue);

        List<MemberLevel> memberLevels = this.list(queryWrapper);
        return memberLevels.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean createMemberLevel(MemberLevelDTO memberLevelDTO) {
        // 验证成长值区间是否有效
        if (!validateGrowthRange(memberLevelDTO.getMinValue(), memberLevelDTO.getMaxValue(), null)) {
            throw new RuntimeException("成长值区间无效或与其他等级重叠");
        }

        // 验证折扣范围
        if (memberLevelDTO.getDiscount().compareTo(BigDecimal.ZERO) <= 0 ||
                memberLevelDTO.getDiscount().compareTo(new BigDecimal("1")) > 0) {
            throw new RuntimeException("折扣必须在0-1之间");
        }

        MemberLevel memberLevel = convertToEntity(memberLevelDTO);
        return this.save(memberLevel);
    }

    @Override
    public boolean updateMemberLevel(MemberLevelDTO memberLevelDTO) {
        if (memberLevelDTO.getId() == null) {
            throw new RuntimeException("会员等级ID不能为空");
        }

        // 检查记录是否存在
        MemberLevel existing = this.getById(memberLevelDTO.getId());
        if (existing == null) {
            throw new RuntimeException("会员等级不存在");
        }

        // 验证成长值区间是否有效
        if (!validateGrowthRange(memberLevelDTO.getMinValue(), memberLevelDTO.getMaxValue(), memberLevelDTO.getId())) {
            throw new RuntimeException("成长值区间无效或与其他等级重叠");
        }

        // 验证折扣范围
        if (memberLevelDTO.getDiscount().compareTo(BigDecimal.ZERO) <= 0 ||
                memberLevelDTO.getDiscount().compareTo(new BigDecimal("1")) > 0) {
            throw new RuntimeException("折扣必须在0-1之间");
        }

        MemberLevel memberLevel = convertToEntity(memberLevelDTO);
        memberLevel.setUpdatedAt(LocalDateTime.now());
        return this.updateById(memberLevel);
    }

    @Override
    public boolean deleteMemberLevel(Integer id) {
        MemberLevel memberLevel = this.getById(id);
        if (memberLevel == null) {
            throw new RuntimeException("会员等级不存在");
        }
        return this.removeById(id);
    }

    @Override
    public MemberLevelVO getLevelByGrowthValue(Integer growValue) {
        LambdaQueryWrapper<MemberLevel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(MemberLevel::getMinValue, growValue)
                .ge(MemberLevel::getMaxValue, growValue)
                .last("LIMIT 1");

        MemberLevel memberLevel = this.getOne(queryWrapper);
        if (memberLevel == null) {
            throw new RuntimeException("未找到对应的会员等级");
        }
        return convertToVO(memberLevel);
    }

    @Override
    public boolean validateGrowthRange(Integer minValue, Integer maxValue, Integer excludeId) {
        if (minValue >= maxValue) {
            return false; // 最小值不能大于等于最大值
        }

        LambdaQueryWrapper<MemberLevel> queryWrapper = new LambdaQueryWrapper<>();

        // 检查区间重叠：新区间与现有区间有重叠
        queryWrapper.and(wrapper -> wrapper
                .between(MemberLevel::getMinValue, minValue, maxValue)
//                .or(between(MemberLevel::getMaxValue, minValue, maxValue))
                .or(w -> w.le(MemberLevel::getMinValue, minValue).ge(MemberLevel::getMaxValue, maxValue))
        );

        if (excludeId != null) {
            queryWrapper.ne(MemberLevel::getId, excludeId);
        }

        long count = this.count(queryWrapper);
        return count == 0; // 没有重叠区间则有效
    }

    private MemberLevelVO convertToVO(MemberLevel memberLevel) {
        MemberLevelVO vo = new MemberLevelVO();
        BeanUtils.copyProperties(memberLevel, vo);
        return vo;
    }

    private MemberLevel convertToEntity(MemberLevelDTO dto) {
        MemberLevel entity = new MemberLevel();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}