package com.lifelist.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lifelist.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
