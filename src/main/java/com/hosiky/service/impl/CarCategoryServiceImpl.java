package com.hosiky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.CarCategoryDTO;
import com.hosiky.domain.po.Brand;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarCategoryServiceImpl extends ServiceImpl<CarCategoryMapper,CarCategory> implements ICarCategoryService {

    private CarMapper carMapper;

    @Override
    public Page<CarCategory> getBrandsByPage(int page, int rows) {
        Page<CarCategory> carCategoryPage = new Page<>(page, rows);
//        给予的查询条件
        LambdaQueryWrapper<CarCategory> brandLambdaQueryWrapper = new LambdaQueryWrapper<>();
        brandLambdaQueryWrapper.eq(CarCategory::getDeleted,0);
        return this.page(carCategoryPage, brandLambdaQueryWrapper);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result deleteByIds(List<Long> ids) {
        if(ids == null || ids.isEmpty()) {
            return Result.errorMsg("删除的ID列表不能为空");
        }

        try {
            for(Long id : ids) {
                carMapper.deleteById(id);
            }

            boolean isSuccess = removeBatchByIds(ids);
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
        carCategory.setDeleted(0);
        carCategory.setCreatedAt(LocalDateTime.now());
        carCategory.setUpdatedAt(LocalDateTime.now());
        this.save(carCategory);
        CarCategoryVo carCategoryVo = new CarCategoryVo();
        BeanUtils.copyProperties(carCategoryDto, carCategoryVo);
        return carCategoryVo;
    }

    @Override
    public CarCategoryVo getByCarCategoryId(Integer id) {
        CarCategoryVo carCategoryVo = new CarCategoryVo();
        CarCategory carCategory = this.getById(id);
        BeanUtils.copyProperties(carCategory, carCategoryVo);
        return carCategoryVo;
    }
}
