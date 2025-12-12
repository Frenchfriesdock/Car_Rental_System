package com.hosiky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.domain.po.CarCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CarCategoryMapper extends BaseMapper<CarCategory> {

    @Select("select * from car_category where id = #{categoryId}")
    CarCategory getByCarCategoryId(String categoryId);
}
