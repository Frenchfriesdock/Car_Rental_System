package com.hosiky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.domain.dto.PointsRecordDTO;
import com.hosiky.domain.po.PointsRecord;
import com.hosiky.domain.vo.PointsRecordVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IPointsRecordService extends IService <PointsRecord> {

    boolean createPointsRecord(@Valid PointsRecordDTO pointsRecordDTO);

    Page<PointsRecordVO> getPointsRecordPage(@NotNull Integer userId, Integer page, Integer size);

    PointsRecordVO getPointsRecordById(Integer id);

    Integer getUserTotalPoints(Integer userId);

    boolean batchCreatePointsRecord(@Valid List<PointsRecordDTO> pointsRecordDTOList);
}
