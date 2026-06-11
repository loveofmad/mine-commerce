package com.mine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mine.model.entity.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
