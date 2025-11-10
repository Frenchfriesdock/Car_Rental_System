package com.hosiky.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.domain.po.Car;
import com.hosiky.mapper.CarMapper;
import com.hosiky.service.ICarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarServiceImpl extends ServiceImpl<CarMapper , Car> implements ICarService {

    
}
