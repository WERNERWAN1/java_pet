package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Foster;

import java.util.List;
import java.util.Map;

public interface FosterMapper extends BaseMapper<Foster> {
    List<Map<String, Object>> getFosterList();
}