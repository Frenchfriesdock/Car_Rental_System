package com.hosiky.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.Result;
import com.hosiky.domain.po.Brand;
import com.hosiky.domain.po.CarCategory;
import com.hosiky.service.ICarCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "车型接口")
@RestController
@RequestMapping("/car_category")
@RequiredArgsConstructor
@Slf4j
public class CarCategoryController {

    private final ICarCategoryService carCategoryService;

    @PostMapping("/save")
    @Operation(summary = "添加分类")
    public Result add(CarCategory carCategory) {
        return new Result(carCategoryService.save(carCategory));
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有的分类")
    public Result list() {
        return Result.ok(carCategoryService.list());
    }

    @GetMapping("/listPage")
    @Operation(summary = "分页查询品牌")
    public Result listPage(@RequestParam int page, @RequestParam int rows) {

        // 参数校验
        if (page < 1) page = 1;
        if (rows < 1 || rows > 100) rows = 10; // 限制每页大小，防止恶意请求

        Page<CarCategory> carCategoryPage = carCategoryService.getBrandsByPage(page, rows);
        return Result.ok(carCategoryPage);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "删除品牌")
    public Result delete(@RequestBody List<Long> ids) {
        return carCategoryService.deleteByIds(ids);
    }

    @GetMapping("/getById")
    public Result getById(@RequestParam Integer id) {
        return Result.ok(carCategoryService.getById(id));
    }
}
