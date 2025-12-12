package com.hosiky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.common.PageParameter;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.CarCategoryDTO;
import com.hosiky.domain.po.Brand;
import com.hosiky.domain.po.Car;
import com.hosiky.domain.po.CarCategory;
import com.hosiky.domain.vo.CarCategoryVo;
import com.hosiky.mapper.CarCategoryMapper;
import com.hosiky.mapper.CarMapper;
import com.hosiky.service.ICarCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarCategoryServiceImpl extends ServiceImpl<CarCategoryMapper,CarCategory> implements ICarCategoryService {

    private final CarServiceImpl carServiceImpl;
    private final CarMapper carMapper;

    @Override
    public Page<CarCategory> getBrandsByPage(PageParameter<CarCategoryDTO> pageParameter) {
        Page<CarCategory> carCategoryPage = new Page<>(pageParameter.getPage(),pageParameter.getLimit());
//        给予的查询条件
        LambdaQueryWrapper<CarCategory> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.hasText(pageParameter.getData().getName())) {
            queryWrapper.like(CarCategory::getName, pageParameter.getData().getName());
        }
        return this.page(carCategoryPage, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteByIds(List<String> ids) {
        if(ids == null || ids.isEmpty()) {
            return Result.errorMsg("删除的ID列表不能为空");
        }

        try {
            for(String id : ids) {
                LambdaUpdateWrapper<Car> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Car::getCategoryId, id)
                        .set(Car::getDeleted, 1);
                carMapper.update(null, updateWrapper);
            }

//            删除分类数据
            CarCategory carCategory = new CarCategory();
            carCategory.setUpdatedAt(LocalDateTime.now());
            LambdaUpdateWrapper<CarCategory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(CarCategory::getDeleted,1);
            updateWrapper.in(CarCategory::getId,ids);

            boolean isSuccess = this.update(carCategory,updateWrapper);
            if(isSuccess) {
                return Result.ok("批量删除成功");
            } else {
                return Result.errorMsg("批量删除失败");
            }
        }catch (Exception e) {
            log.error("批量删除品牌失败: ids={}", ids, e);
            throw  new RuntimeException(e);
        }
    }

    @Override
    public CarCategoryVo create(CarCategoryDTO carCategoryDto) {
        CarCategory carCategory = new CarCategory();
        BeanUtils.copyProperties(carCategoryDto, carCategory);
        carCategory.setId(UUID.randomUUID().toString().replace("-", ""));
        carCategory.setDeleted(0);
        carCategory.setCreatedAt(LocalDateTime.now());
        carCategory.setUpdatedAt(LocalDateTime.now());
        this.save(carCategory);
        CarCategoryVo carCategoryVo = new CarCategoryVo();
        BeanUtils.copyProperties(carCategoryDto, carCategoryVo);
        return carCategoryVo;
    }

    @Override
    public CarCategoryVo getByCarCategoryId(String id) {
        CarCategoryVo carCategoryVo = new CarCategoryVo();
        CarCategory carCategory = this.getById(id);
        BeanUtils.copyProperties(carCategory, carCategoryVo);
        return carCategoryVo;
    }
}
