package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.PetProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetProductMapper extends BaseMapper<PetProduct> {
}
