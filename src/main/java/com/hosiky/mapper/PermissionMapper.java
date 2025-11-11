package com.hosiky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.domain.po.Permission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
