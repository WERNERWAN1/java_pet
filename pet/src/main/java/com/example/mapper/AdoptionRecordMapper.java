package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.AdoptionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdoptionRecordMapper extends BaseMapper<AdoptionRecord> {
    List<Map<String, Object>> getAdoptionRecordList(@Param("breed") String breed, @Param("state") String state);
}
