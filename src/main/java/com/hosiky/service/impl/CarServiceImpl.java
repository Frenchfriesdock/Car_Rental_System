package com.hosiky.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.common.PageResult;
import com.hosiky.domain.dto.carDto.CarDTO;
import com.hosiky.domain.dto.carDto.CarQueryDTO;
import com.hosiky.domain.po.Car;
import com.hosiky.domain.po.CarCategory;
import com.hosiky.domain.vo.carVo.CarVO;
import com.hosiky.mapper.BrandMapper;
import com.hosiky.mapper.CarCategoryMapper;
import com.hosiky.mapper.CarMapper;
import com.hosiky.mapper.MerchantMapper;
import com.hosiky.service.ICarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl extends ServiceImpl<CarMapper , Car> implements ICarService {

   private final CarMapper carMapper;

   private final BrandMapper brandMapper;

   private final MerchantMapper merchantMapper;

   private final CarCategoryMapper carCategoryMapper;

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
    public CarVO updateCar(CarDTO carDTO) {
     if(StringUtils.isEmpty(carDTO.getId())){
      throw new RuntimeException("没有更新汽车id，无法更新");
     }

     Car car = carMapper.getCarById(carDTO.getId());
//     Car car = this.getById(carDTO.getId());
//     CarVO car = this.getCarById(carDTO.getId());
     if (null == car) {
      throw new RuntimeException("更新Car对象不存在");
     }

     LambdaUpdateWrapper<Car> updateWrapper = new LambdaUpdateWrapper<>();
     updateWrapper.eq(Car::getId , carDTO.getId())
             .set(carDTO.getPlateNo() != null, Car::getPlateNo, carDTO.getPlateNo())
             .set(carDTO.getModel() != null, Car::getModel, carDTO.getModel())
             .set(carDTO.getBrandId() != null, Car::getBrandId, carDTO.getBrandId())
             .set(carDTO.getCategoryId() != null, Car::getCategoryId, carDTO.getCategoryId())
             .set(carDTO.getMerchantId() != null, Car::getMerchantId, carDTO.getMerchantId())
             .set(carDTO.getPriceDaily() != null, Car::getPriceDaily, carDTO.getPriceDaily())
             .set(carDTO.getStatus() != null, Car::getStatus, carDTO.getStatus())
             .set(carDTO.getImgUrl() != null, Car::getImgUrl, carDTO.getImgUrl());

     boolean isSuccess = update(updateWrapper);
     if(!isSuccess){
      throw new RuntimeException("Car更新失败");
     }

     return getCarById(carDTO.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CarVO getCarById(Integer id) {
//   getById这个方法在执行的过程当中，会自己考虑到逻辑查询这个点，也就是delete == 0
         Car car = this.getById(id);
         CarVO carVO = new CarVO();
         BeanUtils.copyProperties(car, carVO);
//         给vo的属性赋值
         carVO.setBrandName(brandMapper.getByBrandId(carVO.getBrandId()).getName());
         carVO.setMerchantName(merchantMapper.getByMerchantId(carVO.getMerchantId()).getCompany());
         carVO.setCategoryName(carCategoryMapper.getByCarCategoryId(carVO.getCategoryId()).getName());
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

//     List<CarVO> carVOList = new ArrayList<>();
     // 使用Page的convert方法，一行代码完成转换
     IPage<CarVO> carVOPage = carPage.convert(car -> {
      CarVO carVO = new CarVO();
      BeanUtils.copyProperties(car, carVO);
      return carVO;
     });

     return new PageResult<>(
             carPage.getTotal(),
             carVOPage.getRecords(),
             carPage.getCurrent(),
             carPage.getSize()
     );
    }
}
