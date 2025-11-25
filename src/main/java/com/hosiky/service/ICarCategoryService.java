package com.hosiky.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hosiky.common.Result;
import com.hosiky.domain.dto.CarCategoryDTO;
import com.hosiky.domain.po.Brand;
import com.hosiky.domain.po.CarCategory;
import com.hosiky.domain.vo.CarCategoryVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ICarCategoryService extends IService<CarCategory> {

    Page<CarCategory> getBrandsByPage(int page, int rows);

    Result deleteByIds(List<Long> ids);

    CarCategoryVo create(CarCategoryDTO carCategoryDto);

    CarCategoryVo getByCarCategoryId(Integer id);
}
