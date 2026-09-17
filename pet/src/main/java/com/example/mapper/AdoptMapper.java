package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.Adopt;

import java.util.List;
import java.util.Map;

public interface AdoptMapper extends BaseMapper<Adopt> {
    List<Map<String, Object>> getAdoptList();
}