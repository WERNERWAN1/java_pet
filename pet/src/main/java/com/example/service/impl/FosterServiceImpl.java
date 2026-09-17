package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Foster;
import com.example.mapper.FosterMapper;
import com.example.service.FosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FosterServiceImpl extends ServiceImpl<FosterMapper, Foster> implements FosterService {

    @Autowired
    FosterMapper fosterMapper;

    @Override
    public List<Map<String, Object>> getFosterList() {
        return fosterMapper.getFosterList();
    }

    @Override
    public void addFoster(Foster foster) {
        fosterMapper.insert(foster);
    }

    @Override
    public void updateFoster(Foster foster) {
        fosterMapper.updateById(foster);
    }

    @Override
    public void deleteFoster(Integer orderId) {
        fosterMapper.deleteById(orderId);
    }
}