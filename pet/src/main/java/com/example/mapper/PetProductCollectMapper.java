package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.PetProductCollect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PetProductCollectMapper extends BaseMapper<PetProductCollect> {

    List<Map> getPetProductCollectList(String userId);
}
