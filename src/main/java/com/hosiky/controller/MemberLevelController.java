package com.hosiky.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.MemberLevelDTO;
import com.hosiky.domain.vo.MemberLevelVO;
import com.hosiky.service.IMemberLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "会员等级管理")
@RestController
@RequestMapping("/member/level")
@RequiredArgsConstructor
@Slf4j
@Valid
public class MemberLevelController {

    private IMemberLevelService memberLevelService;

    /**
     * todo 分页查询的接口有问题
     * @param page
     * @param size
     * @param levelName
     * @return
     */
    @Operation(summary = "分页查询会员等级")
    @GetMapping("/page")
    public Result getMemberLevelPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String levelName) {

        Page<MemberLevelVO> result = memberLevelService.getMemberLevelPage(page, size, levelName);
        return Result.ok(result);
    }

    @Operation(summary = "根据ID查询会员等级")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        MemberLevelVO memberLevelVO = memberLevelService.getMemberLevelById(id);
        return Result.ok(memberLevelVO);
    }

    @Operation(summary = "获取所有有效会员等级列表")
    @GetMapping("/list")
    public Result getActiveMemberLevels() {
        List<MemberLevelVO> list = memberLevelService.getActiveMemberLevels();
        return Result.ok(list);
    }

    @Operation(summary = "新增会员等级")
    @PostMapping
    public Result createMemberLevel(@Valid @RequestBody MemberLevelDTO memberLevelDTO) {
        boolean success = memberLevelService.createMemberLevel(memberLevelDTO);
        return success ? Result.ok("会员等级创建成功") : Result.errorMsg("会员等级创建失败");
    }

    @Operation(summary = "修改会员等级")
    @PutMapping
    public Result updateMemberLevel(@Valid @RequestBody MemberLevelDTO memberLevelDTO) {
        boolean success = memberLevelService.updateMemberLevel(memberLevelDTO);
        return success ? Result.ok("会员等级更新成功") : Result.errorMsg("会员等级更新失败");
    }

    @Operation(summary = "删除会员等级")
    @DeleteMapping("/{id}")
    public Result deleteMemberLevel(@PathVariable Integer id) {
        boolean success = memberLevelService.deleteMemberLevel(id);
        return success ? Result.ok("会员等级删除成功") : Result.errorMsg("会员等级删除失败");
    }

    @Operation(summary = "根据成长值获取对应会员等级")
    @GetMapping("/by-growth/{growValue}")
    public Result getLevelByGrowthValue(@PathVariable Integer growValue) {
        MemberLevelVO memberLevelVO = memberLevelService.getLevelByGrowthValue(growValue);
        return Result.ok(memberLevelVO);
    }
}
