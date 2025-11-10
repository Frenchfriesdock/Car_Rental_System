package com.hosiky.controller;

import com.hosiky.common.Result;
import com.hosiky.domain.po.Car;
import com.hosiky.service.ICarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "车辆接口")
@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
@Slf4j
public class CarController {

    private ICarService carService;

    @PostMapping("/add")
    @Operation(summary = "添加汽车信息")
    public Result addCar(Car car) {
        return Result.ok();
    }

    @PutMapping("/update")
    @Operation(summary = "修改汽车信息")
    public Result updateCar(Car car) {
        return Result.ok();
    }

    @GetMapping("/car_id")
    @Operation(summary = "根据car_id查看汽车信息")
    public Result getCarById(int id) {
        return Result.ok();
    }

    @DeleteMapping("/car_id")
    public Result deleteCarById(int id) {
        return Result.ok();
    }

}
