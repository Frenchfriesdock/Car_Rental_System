package com.hosiky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.domain.po.RateRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RateRuleMapper extends BaseMapper<RateRule> {


    @Select("SELECT COUNT(*) FROM rate_rule WHERE brand_id = #{brandId} AND category_id = #{categoryId} AND is_weekend = #{isWeekend} AND deleted = 0 AND id != #{excludeId}")
    Integer countDuplicateRules(Integer brandId, Integer categoryId, Integer isWeekend, int i);


    @Select("SELECT * FROM rate_rule WHERE brand_id = #{brandId} AND category_id = #{categoryId} AND is_weekend = #{isWeekend} AND deleted = 0 LIMIT 1")
    RateRule selectByConditions(Integer brandId, Integer categoryId, Integer isWeekend);


}
