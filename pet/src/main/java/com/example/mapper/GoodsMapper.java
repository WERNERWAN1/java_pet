package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    @Update("UPDATE goods SET num = num - #{count} WHERE id = #{id} AND num >= #{count}")
    int decrementStock(@Param("id") Long id, @Param("count") int count);

    @Update("UPDATE goods SET num = num + #{count} WHERE id = #{id}")
    int incrementStock(@Param("id") Long id, @Param("count") int count);
}
