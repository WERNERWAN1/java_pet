package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.AdMapper;
import com.example.service.AdService;
import com.example.entity.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AdServiceImpl extends ServiceImpl<AdMapper, Ad> implements AdService {
    @Autowired
    AdMapper mapper;

    @Override
    public List<Ad> getAdList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    @Override
    public List<Ad> getValidAdList() {
        QueryWrapper<Ad> queryWrapper = new QueryWrapper<>();
        // 未设置失效时间（长期有效）或失效时间尚未到达的广告才展示
        queryWrapper.and(w -> w.isNull("expire_time").or().gt("expire_time", new Date()));
        queryWrapper.orderByDesc("id");
        return mapper.selectList(queryWrapper);
    }

    @Override
    public void createAd(Ad ad) {
        System.out.println(ad);
        ad.setCreateTime(new Date());
        mapper.insert(ad);
    }

    @Override
    public void deleteAd(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateAd(Ad ad) {
        mapper.updateById(ad);
    }
}
