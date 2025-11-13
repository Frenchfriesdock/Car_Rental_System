package com.hosiky.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.Result;
import com.hosiky.domain.po.AccountRecord;
import com.hosiky.domain.vo.AccountStatisticsVO;
import com.hosiky.service.IAccountRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Tag(name = "账户流水管理")
@RestController
@RequestMapping("/account/record")
@RequiredArgsConstructor
@Slf4j
public class AccountRecordController {

    private final IAccountRecordService accountRecordService;

    @Operation(summary = "分页查询账户流水")
    @GetMapping("/page")
    public Result getRecordPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Page<AccountRecord> recordPage = accountRecordService.getRecordPage(
                page, size, userId, type, orderSn, startTime, endTime);
        return Result.ok(recordPage);
    }

    @Operation(summary = "根据ID查询流水详情")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        AccountRecord record = accountRecordService.getById(id);
        return record != null ? Result.ok(record) : Result.errorMsg("流水记录不存在");
    }

    @Operation(summary = "根据用户ID查询流水列表")
    @GetMapping("/user/{userId}")
    public Result getByUserId(
            @PathVariable Long userId) {
        AccountRecord records = accountRecordService.getById(userId);
        return Result.ok(records);
    }

    @Operation(summary = "创建账户流水记录")
    @PostMapping("/add")
    public Result createRecord(@Valid @RequestBody AccountRecord accountRecord) {
        boolean success = accountRecordService.createRecord(accountRecord);
        return success ? Result.ok("流水记录创建成功") : Result.errorMsg("流水记录创建失败");
    }

    @Operation(summary = "更新账户流水记录")
    @PutMapping("/update")
    public Result  updateRecord(@Valid @RequestBody AccountRecord accountRecord) {
        boolean success = accountRecordService.updateById(accountRecord);
        return success ? Result.ok("流水记录更新成功") : Result.errorMsg("流水记录更新失败");
    }

    @Operation(summary = "删除账户流水记录（逻辑删除）")
    @DeleteMapping("/{id}")
    public Result deleteRecord(@PathVariable Long id) {
        boolean success = accountRecordService.removeById(id);
        return success ? Result.ok("流水记录删除成功") : Result.errorMsg("流水记录删除失败");
    }

    @Operation(summary = "统计用户账户变动")
    @GetMapping("/statistics/{userId}")
    public Result getAccountStatistics(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        AccountStatisticsVO statistics = accountRecordService.getAccountStatistics(userId, type, startTime, endTime);
        return Result.ok(statistics);
    }
}