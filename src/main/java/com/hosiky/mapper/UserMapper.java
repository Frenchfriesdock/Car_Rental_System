package com.hosiky.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hosiky.common.ByPageResult;
import com.hosiky.common.PageParameter;
import com.hosiky.domain.po.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    ByPageResult<User> getUserList(PageParameter<User> userQuery);


//    @Select("select * from user")
    User getUserTest(Integer id);

    @Delete("delete  from user where deleted = '1'")
    void deleteUserTruly();
}
