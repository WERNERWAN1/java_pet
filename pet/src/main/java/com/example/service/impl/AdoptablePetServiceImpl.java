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
        queryWrapper.orderByDesc("pid");
        return adoptablePetMapper.selectList(queryWrapper);
    }

    @Override
    public void addAdoptablePet(AdoptablePet adoptablePet) {
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
