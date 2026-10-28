package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.AdoptablePet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdoptablePetMapper extends BaseMapper<AdoptablePet> {
}
