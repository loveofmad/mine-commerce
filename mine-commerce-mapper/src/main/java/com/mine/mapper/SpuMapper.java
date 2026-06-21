package com.mine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mine.model.entity.Spu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SpuMapper extends BaseMapper<Spu> {

    @Update("UPDATE tb_spu SET stock = stock - #{quantity}, update_time = NOW() WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE tb_spu SET sales = sales + #{quantity}, update_time = NOW() WHERE id = #{id}")
    int increaseSales(@Param("id") Long id, @Param("quantity") Integer quantity);
}
