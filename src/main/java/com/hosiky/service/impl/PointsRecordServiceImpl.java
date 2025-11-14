package com.hosiky.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.domain.dto.PointsRecordDTO;
import com.hosiky.domain.po.PointsRecord;
import com.hosiky.domain.vo.PointsRecordVO;
import com.hosiky.mapper.PointsRecordMapper;
import com.hosiky.service.IPointsRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements IPointsRecordService {


    private PointsRecordMapper pointsRecordMapper;

    @Override
    public boolean createPointsRecord(PointsRecordDTO pointsRecordDTO) {
//        参数验证
        Assert.notNull(pointsRecordDTO,"积分记录数据不能为空");
        Assert.notNull(pointsRecordDTO.getUserId(),"用户ID不能为空");
        Assert.notNull(pointsRecordDTO.getPoints(),"积分变动值不能为空");

        if(pointsRecordDTO.getPoints() == 0) {
            throw new IllegalArgumentException("积分变动值不能为0");
        }

        PointsRecord pointsRecord = new PointsRecord();
        BeanUtils.copyProperties(pointsRecordDTO,pointsRecord);
        pointsRecord.setCreatedAt(LocalDateTime.now());

        boolean success = this.save(pointsRecord);
        if (success) {
            log.info("创建积分流水成功: 用户ID={}, 积分变动={}, 备注={}",
                    pointsRecordDTO.getUserId(), pointsRecordDTO.getPoints(),
                    pointsRecordDTO.getRemark());
        } else {
            log.error("创建积分流水失败: 用户ID={}", pointsRecordDTO.getUserId());
        }

        return success;
    }

    @Override
    public Page<PointsRecordVO> getPointsRecordPage(Long userId, Integer page, Integer size) {
//        参数校验
        Assert.notNull(userId,"用户ID不能为空");
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 100) size = 10;

//        构建查询条件
        LambdaQueryWrapper<PointsRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getCreatedAt);

//        执行分页查询
        Page<PointsRecord> entityPage = new Page<>(page, size);
        Page<PointsRecord> resultPage = this.page(entityPage, queryWrapper);

        // 转换为VO
        return (Page<PointsRecordVO>) resultPage.convert(this::convertToVO);
    }

    @Override
    public PointsRecordVO getPointsRecordById(Long id) {
        PointsRecord pointsRecord = this.getById(id);
        if (pointsRecord == null) {
            throw new RuntimeException("积分记录不存在");
        }
        return convertToVO(pointsRecord);
    }

    @Override
    public Integer getUserTotalPoints(Long userId) {

        Assert.notNull(userId, "用户ID不能为空");
        Integer totalPoints = pointsRecordMapper.sumPointsByUserId(userId);
        return totalPoints != null ? totalPoints : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreatePointsRecord(List<PointsRecordDTO> pointsRecordDTOList) {
        if (pointsRecordDTOList == null || pointsRecordDTOList.isEmpty()) {
            return true;
        }

        List<PointsRecord> pointsRecords = pointsRecordDTOList.stream()
                .map(dto -> {
                    PointsRecord record = new PointsRecord();
                    BeanUtils.copyProperties(dto, record);
                    record.setCreatedAt(LocalDateTime.now());
                    return record;
                })
                .collect(Collectors.toList());

        return this.saveBatch(pointsRecords);
    }


    /**
     * 将Entity转换为VO
     */
    private PointsRecordVO convertToVO(PointsRecord entity) {
        if (entity == null) {
            return null;
        }
        PointsRecordVO vo = new PointsRecordVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

}
