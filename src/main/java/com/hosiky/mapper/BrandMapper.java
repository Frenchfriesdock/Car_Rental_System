package com.hosiky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.domain.po.Brand;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BrandMapper extends BaseMapper<Brand> {

    @Select("select * from brand where id = #{brandId}")
    Brand getByBrandId(Integer brandId);
}
