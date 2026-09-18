package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Foster;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FosterMapper extends BaseMapper<Foster> {
    List<Map<String, Object>> getFosterList(@Param("progress") String progress);
}