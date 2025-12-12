package com.hosiky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.domain.po.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BrandMapper extends BaseMapper<Brand> {

    @Select("select * from brand where id = #{brandId}")
    Brand getByBrandId(String brandId);

    @Delete("<script>" +
            "DELETE FROM brand WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    boolean removeBatchByIdsTrue(@Param("ids") List<String> ids); // 注意：这里添加了 @Param 注解
}
