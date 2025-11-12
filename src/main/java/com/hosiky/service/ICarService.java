package com.hosiky.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.common.PageResult;
import com.hosiky.domain.dto.carDto.CarDTO;
import com.hosiky.domain.dto.carDto.CarQueryDTO;
import com.hosiky.domain.po.Car;
import com.hosiky.domain.vo.carVo.CarVO;
import org.springframework.stereotype.Service;

@Service
public interface ICarService extends IService<Car> {


    void addCar(CarDTO carDTO);

    void updateCar(CarDTO carDTO);

    CarVO getCarById(Integer id);

    void deleteCarById(Integer id);

    PageResult<CarVO> getCarList(CarQueryDTO queryDTO);
}
