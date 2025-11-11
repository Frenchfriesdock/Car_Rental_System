package com.hosiky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.domain.po.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 */

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}
