package com.hosiky.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "车辆接口")
@RestController
@RequestMapping("/car")
@RequiredArgsConstructor
@Slf4j
public class CarController {


}
