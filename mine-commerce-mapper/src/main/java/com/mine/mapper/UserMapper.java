package com.mine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mine.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
