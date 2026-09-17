package com.example.service.impl;

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
    public List<Map<String, Object>> getAdoptList() {
        return adoptMapper.getAdoptList();
    }

    @Override
    public void addAdopt(Adopt adopt) {
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