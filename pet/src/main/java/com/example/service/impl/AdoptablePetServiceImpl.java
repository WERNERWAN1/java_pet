package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.AdoptablePet;
import com.example.mapper.AdoptablePetMapper;
import com.example.service.AdoptablePetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdoptablePetServiceImpl extends ServiceImpl<AdoptablePetMapper, AdoptablePet> implements AdoptablePetService {

    @Autowired
    AdoptablePetMapper adoptablePetMapper;

    @Override
    public List<AdoptablePet> getAdoptablePetList(String keyword) {
        QueryWrapper<AdoptablePet> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("nickname", keyword);
        }
        // 待领养(status=1)优先展示，已领养(status=2)排在后面；同状态内按 pid 倒序（新增在前）
        queryWrapper.orderByAsc("status").orderByDesc("pid");
        return adoptablePetMapper.selectList(queryWrapper);
    }

    @Override
    public void addAdoptablePet(AdoptablePet adoptablePet) {
        // 新建宠物默认处于「待领养」(status=1) 状态
        if (adoptablePet.getStatus() == null) {
            adoptablePet.setStatus(1);
        }
        adoptablePetMapper.insert(adoptablePet);
    }

    @Override
    public void updateAdoptablePet(AdoptablePet adoptablePet) {
        adoptablePetMapper.updateById(adoptablePet);
    }

    @Override
    public void deleteAdoptablePet(Integer pid) {
        adoptablePetMapper.deleteById(pid);
    }

    @Override
    public AdoptablePet getAdoptablePetById(Integer pid) {
        return adoptablePetMapper.selectById(pid);
    }
}
