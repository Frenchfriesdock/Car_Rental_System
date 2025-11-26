package com.hosiky.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.Result;
import com.hosiky.domain.vo.PointsRecordVO;
import com.hosiky.domain.dto.PointsRecordDTO;
import com.hosiky.service.IPointsRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "积分流水管理")
@RestController
@RequestMapping("/points/record")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PointsRecordController {

    private final IPointsRecordService pointsRecordService;

    @Operation(summary = "创建积分流水记录")
    @PostMapping
    public Result createPointsRecord(@Valid @RequestBody PointsRecordDTO pointsRecordDTO) {
        try {
            boolean success = pointsRecordService.createPointsRecord(pointsRecordDTO);
            return success ? Result.ok("积分记录创建成功") : Result.errorMsg("积分记录创建失败");
        } catch (IllegalArgumentException e) {
            log.warn("参数验证失败: {}", e.getMessage());
            return Result.errorMsg(e.getMessage());
        } catch (Exception e) {
            log.error("创建积分记录异常: {}", e.getMessage(), e);
            return Result.errorMsg("系统异常，请稍后重试");
        }
    }

    /**
     * @param userId
     * @param page
     * @param size
     * @return
     */
    @Operation(summary = "分页查询用户积分流水")
    @GetMapping("/user/{userId}")
    public Result getPointsRecordPage(
            @PathVariable @NotNull Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        try {
            Page<PointsRecordVO> result = pointsRecordService.getPointsRecordPage(userId, page, size);
            return Result.ok(result);
        } catch (Exception e) {
            log.error("查询积分流水异常: userId={}", userId, e);
            return Result.errorMsg("查询失败");
        }
    }

    /**
     * 这个是主键id，并不是user_id
     * @param id
     * @return
     */
    @Operation(summary = "根据ID查询积分流水详情")
    @GetMapping("/{id}")
    public Result getPointsRecordById(@PathVariable Integer id) {
        try {
            PointsRecordVO pointsRecordVO = pointsRecordService.getPointsRecordById(id);
            return Result.ok(pointsRecordVO);
        } catch (RuntimeException e) {
            log.warn("积分记录不存在: id={}", id);
            return Result.errorMsg(e.getMessage());
        } catch (Exception e) {
            log.error("查询积分记录异常: id={}", id, e);
            return Result.errorMsg("查询失败");
        }
    }

    /**
     * @param userId
     * @return
     */
    @Operation(summary = "获取用户总积分")
    @GetMapping("/user/{userId}/total")
    public Result getUserTotalPoints(@PathVariable Integer userId) {
        try {
            Integer totalPoints = pointsRecordService.getUserTotalPoints(userId);
            return Result.ok(totalPoints);
        } catch (Exception e) {
            log.error("获取用户总积分异常: userId={}", userId, e);
            return Result.errorMsg("获取失败");
        }
    }

    /**
     * 这个接口用的比较少，基本不怎么使用，毕竟有单个创建积分的接口存在
     * @param pointsRecordDTOList
     * @return
     */
    @Operation(summary = "批量创建积分流水")
    @PostMapping("/batch")
    public Result batchCreatePointsRecord(@Valid @RequestBody List<PointsRecordDTO> pointsRecordDTOList) {
        try {
            boolean success = pointsRecordService.batchCreatePointsRecord(pointsRecordDTOList);
            return success ? Result.ok("批量创建成功") : Result.errorMsg("批量创建失败");
        } catch (Exception e) {
            log.error("批量创建积分记录异常", e);
            return Result.errorMsg("批量创建失败");
        }
    }
}