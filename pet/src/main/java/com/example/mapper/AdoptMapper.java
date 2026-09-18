package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Adopt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdoptMapper extends BaseMapper<Adopt> {
    List<Map<String, Object>> getAdoptList(@Param("breed") String breed, @Param("state") String state);
}