package com.mine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mine.model.entity.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SkuMapper extends BaseMapper<Sku> {

    @Update("UPDATE tb_sku SET stock = stock - #{quantity}, update_time = NOW() WHERE id = #{id} AND stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE tb_sku SET sales = sales + #{quantity}, update_time = NOW() WHERE id = #{id}")
    int increaseSales(@Param("id") Long id, @Param("quantity") Integer quantity);
}
