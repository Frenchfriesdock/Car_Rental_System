package com.hosiky.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.BrandDTO;
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

    private final IBrandService brandService;

    @PostMapping("/save")
    @Operation(summary = "添加品牌")
    public Result add(@RequestBody BrandDTO brandDto) {

        return Result.ok(brandService.create(brandDto));
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
    @Operation(summary = "逻辑删除品牌")
    public Result delete(@RequestBody List<Long> ids) {
        return brandService.deleteByIds(ids);
    }

    @GetMapping("/getById")
    @Operation(summary = "根据brandId查询品牌信息")
    public Result getById(@RequestParam Integer id) {
        return Result.ok(brandService.getByBrandId(id));
    }

    /**
     * todo 真正删除还没有实现，可以使用动态sql xml文件来实现
     * @param ids
     * @return
     */
    @DeleteMapping("/batchTrue")
    @Operation(summary = "真正删除brand")
    public Result batchTrue(@RequestBody List<Long> ids) {
        return Result.ok(brandService.deleteByIdsTrue(ids));
    }
}
