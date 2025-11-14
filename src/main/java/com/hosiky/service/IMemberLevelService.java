package com.hosiky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.domain.dto.MemberLevelDTO;
import com.hosiky.domain.po.MemberLevel;
import com.hosiky.domain.vo.MemberLevelVO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IMemberLevelService extends IService<MemberLevel> {
    Page<MemberLevelVO> getMemberLevelPage(Integer page, Integer size, String levelName);

    MemberLevelVO getMemberLevelById(Integer id);

    List<MemberLevelVO> getActiveMemberLevels();

    boolean createMemberLevel(@Valid MemberLevelDTO memberLevelDTO);

    boolean updateMemberLevel(@Valid MemberLevelDTO memberLevelDTO);

    boolean deleteMemberLevel(Integer id);

    MemberLevelVO getLevelByGrowthValue(Integer growValue);

    boolean validateGrowthRange(Integer minValue, Integer maxValue, Integer excludeId);
}
