package com.hosiky.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.hosiky.domain.po.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends MppBaseMapper<UserRole> {
}