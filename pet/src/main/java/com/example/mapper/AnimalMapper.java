package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Animal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnimalMapper extends BaseMapper<Animal> {
}