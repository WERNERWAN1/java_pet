package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.AdoptionRecord;

import java.util.List;
import java.util.Map;

public interface AdoptionRecordService extends IService<AdoptionRecord> {
    List<Map<String, Object>> getAdoptionRecordList(String breed, String state);
    void addAdoptionRecord(AdoptionRecord adoptionRecord);
    void updateAdoptionRecord(AdoptionRecord adoptionRecord);
    void deleteAdoptionRecord(Integer recordId);
}
