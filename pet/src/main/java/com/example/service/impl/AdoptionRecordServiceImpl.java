package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.AdoptablePet;
import com.example.entity.AdoptionRecord;
import com.example.mapper.AdoptablePetMapper;
import com.example.mapper.AdoptionRecordMapper;
import com.example.service.AdoptionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdoptionRecordServiceImpl extends ServiceImpl<AdoptionRecordMapper, AdoptionRecord> implements AdoptionRecordService {

    @Autowired
    AdoptionRecordMapper adoptionRecordMapper;

    @Autowired
    AdoptablePetMapper adoptablePetMapper;

    private static final Set<Integer> VALID_STATES = new HashSet<>(Arrays.asList(1, 2, 3, 4));

    @Override
    public List<Map<String, Object>> getAdoptionRecordList(String breed, String state) {
        return adoptionRecordMapper.getAdoptionRecordList(breed, state);
    }

    @Override
    public void addAdoptionRecord(AdoptionRecord adoptionRecord) {
        if (adoptionRecord.getPetUid() == null) {
            throw new RuntimeException("宠物编号不能为空");
        }
        AdoptablePet pet = adoptablePetMapper.selectById(adoptionRecord.getPetUid());
        if (pet == null) {
            throw new RuntimeException("该宠物不存在");
        }
        if (adoptionRecord.getOwnerUid() == null) {
            throw new RuntimeException("领养人编号不能为空");
        }
        Long petActive = adoptionRecordMapper.selectCount(new QueryWrapper<AdoptionRecord>()
                .eq("pet_uid", adoptionRecord.getPetUid())
                .in("state", 1, 2));
        if (petActive != null && petActive > 0) {
            throw new RuntimeException("该宠物已在领养流程中，不可重复申请");
        }
        Long userActive = adoptionRecordMapper.selectCount(new QueryWrapper<AdoptionRecord>()
                .eq("pet_uid", adoptionRecord.getPetUid())
                .eq("owner_uid", adoptionRecord.getOwnerUid())
                .in("state", 1, 2));
        if (userActive != null && userActive > 0) {
            throw new RuntimeException("您已对该宠物提交过申请");
        }
        adoptionRecord.setState(1);
        adoptionRecordMapper.insert(adoptionRecord);
    }

    @Override
    public void updateAdoptionRecord(AdoptionRecord adoptionRecord) {
        if (adoptionRecord.getState() != null && !VALID_STATES.contains(adoptionRecord.getState())) {
            throw new RuntimeException("无效的状态值");
        }
        if (adoptionRecord.getRecordId() != null && adoptionRecord.getState() != null) {
            AdoptionRecord saved = adoptionRecordMapper.selectById(adoptionRecord.getRecordId());
            if (saved == null) {
                throw new RuntimeException("记录不存在");
            }
            validateStateTransition(saved.getState(), adoptionRecord.getState());
            adoptionRecordMapper.updateById(adoptionRecord);
            if (saved.getPetUid() != null) {
                int petStatus;
                if (adoptionRecord.getState() == 2) {
                    petStatus = 2;
                } else if (adoptionRecord.getState() == 3 || adoptionRecord.getState() == 4) {
                    petStatus = 1;
                } else {
                    return;
                }
                AdoptablePet pet = new AdoptablePet();
                pet.setPid(saved.getPetUid());
                pet.setStatus(petStatus);
                adoptablePetMapper.updateById(pet);
            }
        } else {
            adoptionRecordMapper.updateById(adoptionRecord);
        }
    }

    @Override
    public void deleteAdoptionRecord(Integer recordId) {
        AdoptionRecord saved = adoptionRecordMapper.selectById(recordId);
        if (saved != null && saved.getPetUid() != null && saved.getState() != null && saved.getState() == 2) {
            AdoptablePet pet = new AdoptablePet();
            pet.setPid(saved.getPetUid());
            pet.setStatus(1);
            adoptablePetMapper.updateById(pet);
        }
        adoptionRecordMapper.deleteById(recordId);
    }

    private void validateStateTransition(Integer currentState, Integer newState) {
        if (currentState == null) return;
        boolean valid = false;
        switch (currentState) {
            case 1: valid = (newState == 2 || newState == 3); break;
            case 2: valid = (newState == 3 || newState == 4); break;
            default: break;
        }
        if (!valid) {
            throw new RuntimeException("不允许从状态 " + currentState + " 转换到 " + newState);
        }
    }
}
