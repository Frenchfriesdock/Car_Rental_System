package com.hosiky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.domain.po.Car;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CarMapper extends BaseMapper<Car> {
    @Select("select * from car where id = #{id}")
    Car getCarById(String id);

    @Delete("delete from car where brand_id = #{id}")
    void deleteByIdTrue(String id);

    @Delete("delete from car where brand_id = #{id}")
    void deleteByBrandId(String id);
}
