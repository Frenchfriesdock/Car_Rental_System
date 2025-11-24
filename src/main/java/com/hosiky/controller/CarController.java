package com.hosiky.controller;

import com.hosiky.common.PageResult;
import com.hosiky.common.Result;

import com.hosiky.domain.dto.carDto.CarDTO;
import com.hosiky.domain.dto.carDto.CarQueryDTO;
import com.hosiky.domain.vo.carVo.CarVO;
import com.hosiky.service.ICarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "车辆接口")
@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
@Slf4j
public class CarController {

    private final ICarService carService;

    @PostMapping("/add")
    @Operation(summary = "添加汽车信息")
    public Result addCar(@Validated @RequestBody CarDTO carDTO) {
        carService.addCar(carDTO);
        return Result.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "修改汽车信息")
    public Result updateCar(@Validated @RequestBody CarDTO carDTO) {
        return Result.ok(carService.updateCar(carDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查看汽车详情")
    public Result getCarById(@PathVariable Integer id) {
        CarVO carVO = carService.getCarById(id);
        return Result.ok(carVO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除汽车")
    public Result deleteCarById(@PathVariable Integer id) {
        carService.deleteCarById(id);
        return Result.ok();
    }

    /**
     * @param queryDTO
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询车辆列表")
    public Result getCarList(@Validated CarQueryDTO queryDTO) {
        PageResult<CarVO> pageResult = carService.getCarList(queryDTO);
        return Result.ok(pageResult);
    }
}