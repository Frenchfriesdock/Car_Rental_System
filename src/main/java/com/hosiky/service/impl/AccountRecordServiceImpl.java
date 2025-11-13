package com.hosiky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.domain.po.AccountRecord;
import com.hosiky.domain.vo.AccountStatisticsVO;
import com.hosiky.mapper.AccountRecordMapper;
import com.hosiky.service.IAccountRecordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountRecordServiceImpl extends ServiceImpl<AccountRecordMapper, AccountRecord> implements IAccountRecordService {

    private AccountRecordMapper accountRecordMapper;

    @Override
    public Page<AccountRecord> getRecordPage(Integer page, Integer size, Long userId, Integer type, String orderSn, LocalDateTime startTime, LocalDateTime endTime) {
        Page<AccountRecord> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<AccountRecord> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(userId != null, AccountRecord::getUserId, userId)
                .eq(type != null, AccountRecord::getType, type)
                .like(StringUtils.hasText(orderSn), AccountRecord::getOrderSn, orderSn)
                .ge(startTime != null, AccountRecord::getCreatedAt, startTime)
                .le(endTime != null, AccountRecord::getCreatedAt, endTime)
                .orderByDesc(AccountRecord::getCreatedAt);

        return this.page(pageInfo, queryWrapper);
    }

    @Override
    public boolean createRecord(AccountRecord accountRecord) {
        // 设置创建时间
        accountRecord.setCreatedAt(LocalDateTime.now());
        // 验证金额有效性
        if (accountRecord.getAmount() == null || accountRecord.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("变动金额不能为零");
        }
        return this.save(accountRecord);
    }

    @Override
    public AccountStatisticsVO getAccountStatistics(Long userId, Integer type, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<AccountRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountRecord::getUserId, userId)
                .eq(type != null, AccountRecord::getType, type)
                .ge(startTime != null, AccountRecord::getCreatedAt, startTime)
                .le(endTime != null, AccountRecord::getCreatedAt, endTime);

        List<AccountRecord> records = this.list(queryWrapper);

        BigDecimal totalIncome = records.stream()
                .filter(record -> record.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(AccountRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = records.stream()
                .filter(record -> record.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(AccountRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AccountStatisticsVO(totalIncome, totalExpense, records.size());
    }

}
