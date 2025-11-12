package com.hosiky.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.Result;
import com.hosiky.domain.po.Brand;
import com.hosiky.service.IBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "品牌管理")
@RestController
@RequestMapping("/brand")
@RequiredArgsConstructor
@Slf4j

public class BrandController {

    private IBrandService brandService;

    @PostMapping("/save")
    @Operation(summary = "添加品牌")
    public Result add(@RequestBody Brand brand) {

        return Result.ok(brandService.save(brand));
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有的品牌")
    public Result list() {
        return Result.ok(brandService.list());
    }

    @GetMapping("/listPage")
    @Operation(summary = "分页查询品牌")
    public Result listPage(@RequestParam int page, @RequestParam int rows) {

        // 参数校验
        if (page < 1) page = 1;
        if (rows < 1 || rows > 100) rows = 10; // 限制每页大小，防止恶意请求

        Page<Brand> brandPage = brandService.getBrandsByPage(page, rows);
        return Result.ok(brandPage);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "删除品牌")
    public Result delete(@RequestBody List<Long> ids) {
        return brandService.deleteByIds(ids);
    }

    @GetMapping("/getById")
    public Result getById(@RequestParam Integer id) {
        return Result.ok(brandService.getById(id));
    }
}
