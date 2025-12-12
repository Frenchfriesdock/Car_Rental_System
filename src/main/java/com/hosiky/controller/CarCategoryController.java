package com.hosiky.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hosiky.common.PageParameter;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.CarCategoryDTO;
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
    public Result add(@RequestBody CarCategoryDTO carCategoryDto) {
        return new Result(carCategoryService.create(carCategoryDto));
    }

    @GetMapping("/list")
    @Operation(summary = "查询所有的分类")
    public Result list() {
        return Result.ok(carCategoryService.list());
    }

    @GetMapping("/listPage")
    @Operation(summary = "分页查询分类")
    public Result listPage(@RequestBody PageParameter<CarCategoryDTO> pageParameter) {

        Page<CarCategory> carCategoryPage = carCategoryService.getBrandsByPage(pageParameter);
        return Result.ok(carCategoryPage);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "删除分类")
    public Result delete(@RequestBody List<String> ids) {
        return carCategoryService.deleteByIds(ids);
    }

    @GetMapping("/getById")
    public Result getById(@RequestParam String id) {
        return Result.ok(carCategoryService.getByCarCategoryId(id));
    }
}
