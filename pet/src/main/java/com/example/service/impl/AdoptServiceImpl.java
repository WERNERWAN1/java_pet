package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Adopt;
import com.example.mapper.AdoptMapper;
import com.example.service.AdoptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdoptServiceImpl extends ServiceImpl<AdoptMapper, Adopt> implements AdoptService {

    @Autowired
    AdoptMapper adoptMapper;

    @Override
    public List<Map<String, Object>> getAdoptList(String breed, String state) {
        return adoptMapper.getAdoptList(breed, state);
    }

    /**
     * 新增领养申请。
     * 业务约束：同一只宠物在"待审核"或"已领养"状态下不允许被重复申请，
     * 避免一只宠物同时生成多份有效领养记录，造成后续审核与状态流转混乱。
     */
    @Override
    public void addAdopt(Adopt adopt) {
        if (adopt.getPetUid() != null) {
            Long exists = adoptMapper.selectCount(new QueryWrapper<Adopt>()
                    .eq("pet_uid", adopt.getPetUid())
                    .in("state", "待审核", "已领养"));
            if (exists != null && exists > 0) {
                throw new RuntimeException("该宠物已在领养流程中，不可重复申请");
            }
        }
        adoptMapper.insert(adopt);
    }

    @Override
    public void updateAdopt(Adopt adopt) {
        adoptMapper.updateById(adopt);
    }

    @Override
    public void deleteAdopt(Integer recordId) {
        adoptMapper.deleteById(recordId);
    }
}