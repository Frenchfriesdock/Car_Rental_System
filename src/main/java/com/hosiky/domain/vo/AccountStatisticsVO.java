package com.hosiky.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "账户统计信息")
public class AccountStatisticsVO {
    
    @Schema(description = "总收入金额")
    private BigDecimal totalIncome;
    
    @Schema(description = "总支出金额")
    private BigDecimal totalExpense;
    
    @Schema(description = "交易笔数")
    private Integer transactionCount;
    
    public AccountStatisticsVO(BigDecimal totalIncome, BigDecimal totalExpense, Integer transactionCount) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.transactionCount = transactionCount;
    }
}