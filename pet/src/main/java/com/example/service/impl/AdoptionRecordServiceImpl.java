package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.AdoptionRecord;
import com.example.mapper.AdoptionRecordMapper;
import com.example.service.AdoptionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdoptionRecordServiceImpl extends ServiceImpl<AdoptionRecordMapper, AdoptionRecord> implements AdoptionRecordService {

    @Autowired
    AdoptionRecordMapper adoptionRecordMapper;

    @Override
    public List<Map<String, Object>> getAdoptionRecordList(String breed, String state) {
        return adoptionRecordMapper.getAdoptionRecordList(breed, state);
    }

    /**
     * 新增领养申请。
     * 业务约束：同一只宠物在"待审核"或"已领养"状态下不允许被重复申请，
     * 避免一只宠物同时生成多份有效领养记录，造成后续审核与状态流转混乱。
     */
    @Override
    public void addAdoptionRecord(AdoptionRecord adoptionRecord) {
        if (adoptionRecord.getPetUid() != null) {
            Long exists = adoptionRecordMapper.selectCount(new QueryWrapper<AdoptionRecord>()
                    .eq("pet_uid", adoptionRecord.getPetUid())
                    .in("state", "待审核", "已领养"));
            if (exists != null && exists > 0) {
                throw new RuntimeException("该宠物已在领养流程中，不可重复申请");
            }
        }
        adoptionRecordMapper.insert(adoptionRecord);
    }

    @Override
    public void updateAdoptionRecord(AdoptionRecord adoptionRecord) {
        adoptionRecordMapper.updateById(adoptionRecord);
    }

    @Override
    public void deleteAdoptionRecord(Integer recordId) {
        adoptionRecordMapper.deleteById(recordId);
    }
}
