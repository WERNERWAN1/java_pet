package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.PetProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PetProductMapper extends BaseMapper<PetProduct> {

    @Update("UPDATE pet_product SET stock = stock - #{count} WHERE id = #{id} AND stock >= #{count}")
    int decrementStock(@Param("id") Long id, @Param("count") int count);

    @Update("UPDATE pet_product SET stock = stock + #{count} WHERE id = #{id}")
    int incrementStock(@Param("id") Long id, @Param("count") int count);
}
