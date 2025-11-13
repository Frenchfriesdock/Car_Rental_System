package com.hosiky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.domain.po.AccountRecord;
import com.hosiky.domain.vo.AccountStatisticsVO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public interface IAccountRecordService extends IService<AccountRecord> {

    Page<AccountRecord> getRecordPage(Integer page, Integer size, Long userId, Integer type, String orderSn, LocalDateTime startTime, LocalDateTime endTime);


    boolean createRecord(@Valid AccountRecord accountRecord);

    AccountStatisticsVO getAccountStatistics(Long userId, Integer type, LocalDateTime startTime, LocalDateTime endTime);
}
