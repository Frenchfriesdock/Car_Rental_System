package com.hosiky.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.common.PageResult;
import com.hosiky.domain.dto.carDto.CarDTO;
import com.hosiky.domain.dto.carDto.CarQueryDTO;
import com.hosiky.domain.po.Car;
import com.hosiky.domain.vo.carVo.CarVO;
import com.hosiky.mapper.CarMapper;
import com.hosiky.service.ICarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarServiceImpl extends ServiceImpl<CarMapper , Car> implements ICarService {

   private final CarMapper carMapper;

    @Override
    public void addCar(CarDTO carDTO) {
     Car car = new Car();
     BeanUtils.copyProperties(carDTO, car);
     car.setCreatedAt(LocalDateTime.now());
     car.setUpdatedAt(LocalDateTime.now());
     this.save(car);
//     是否需要vo展示看前端需要，暂定
    }

    @Override
    public void updateCar(CarDTO carDTO) {
     boolean update = lambdaUpdate()
             .eq(carDTO.getId() != null, Car::getId, carDTO.getId())
             .set(carDTO.getPlateNo() != null, Car::getPlateNo, carDTO.getPlateNo())
             .set(carDTO.getModel() != null, Car::getModel, carDTO.getModel())
             .set(carDTO.getBrandId() != null, Car::getBrandId, carDTO.getBrandId())
             .set(carDTO.getCategoryId() != null, Car::getCategoryId, carDTO.getCategoryId())
             .set(carDTO.getMerchantId() != null, Car::getMerchantId, carDTO.getMerchantId())
             .set(carDTO.getPriceDaily() != null, Car::getPriceDaily, carDTO.getPriceDaily())
             .set(carDTO.getStatus() != null, Car::getStatus, carDTO.getStatus())
             .set(carDTO.getImgUrl() != null, Car::getImgUrl, carDTO.getImgUrl())
             .update();

     if (!update) {
      throw new RuntimeException("更新失败");
     }
    }

    @Override
    public CarVO getCarById(Integer id) {
         CarVO carVO = new CarVO();
         Car car = this.getById(id);
         BeanUtils.copyProperties(car, carVO);
        return carVO;

    }

    @Override
    public void deleteCarById(Integer id) {

     boolean update = lambdaUpdate().eq(Car::getId,id)
             .set(Car::getDeleted, 1)
             .update();
     if(!update){
      throw new RuntimeException("删除失败");
     }
    }

    @Override
    public PageResult<CarVO> getCarList(CarQueryDTO queryDTO) {
     Page<Car> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
//    构建查询条件
     LambdaQueryWrapper<Car> queryWrapper = new LambdaQueryWrapper<>();
//     添加查询条件
     if (StringUtils.hasText(queryDTO.getPlateNo())) {
      queryWrapper.like(Car::getPlateNo, queryDTO.getPlateNo());
     }
     if(queryDTO.getBrandId() != null){
      queryWrapper.eq(Car::getBrandId, queryDTO.getBrandId());
     }
     if (queryDTO.getCategoryId() != null) {
      queryWrapper.eq(Car::getCategoryId, queryDTO.getCategoryId());
     }
     if (queryDTO.getMerchantId() != null) {
      queryWrapper.eq(Car::getMerchantId, queryDTO.getMerchantId());
     }
     if (queryDTO.getStatus() != null) {
      queryWrapper.eq(Car::getStatus, queryDTO.getStatus());
     }
     // 价格范围查询
     if (queryDTO.getMinPriceDaily() != null) {
      queryWrapper.ge(Car::getPriceDaily, queryDTO.getMinPriceDaily());
     }
     if (queryDTO.getMaxPriceDaily() != null) {
      queryWrapper.le(Car::getPriceDaily, queryDTO.getMaxPriceDaily());
     }

//     执行分页查询
     Page<Car> carPage = carMapper.selectPage(page, queryWrapper);

     List<CarVO> carVOList = new ArrayList<>();
     BeanUtils.copyProperties(carPage.getRecords(), carVOList);

     return new PageResult<>(
             carPage.getTotal(),
             carVOList,
             carPage.getCurrent(),
             carPage.getSize()
     );
    }
}
