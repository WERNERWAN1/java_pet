package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ThingCollectMapper;
import com.example.mapper.ThingMapper;
import com.example.service.ThingCollectService;
import com.example.entity.Thing;
import com.example.entity.ThingCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class ThingCollectServiceImpl extends ServiceImpl<ThingCollectMapper, ThingCollect> implements ThingCollectService {
    @Autowired
    ThingCollectMapper mapper;

    @Autowired
    ThingMapper thingMapper;

    @Override
    public List<Map> getThingCollectList(String userId) {
        return mapper.getThingCollectList(userId);
    }

    @Override
    public void createThingCollect(ThingCollect thingCollect) {
        mapper.insert(thingCollect);;
    }

    @Override
    public void deleteThingCollect(String id) {
        mapper.deleteById(id);
    }

    @Override
    public ThingCollect getThingCollect(Long userId, Long thingId) {
        QueryWrapper<ThingCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean addCollect(ThingCollect thingCollect) {
        // 检查是否已收藏
        if (getThingCollect(thingCollect.getUserId(), thingCollect.getThingId()) != null) {
            return false;
        }
        
        // 创建收藏
        createThingCollect(thingCollect);
        
        // 更新收藏计数
        Thing thing = thingMapper.selectById(thingCollect.getThingId());
        if (thing != null) {
            thing.setCollectCount((thing.getCollectCount() != null ? thing.getCollectCount() : 0) + 1);
            thingMapper.updateById(thing);
        }
        return true;
    }
}
