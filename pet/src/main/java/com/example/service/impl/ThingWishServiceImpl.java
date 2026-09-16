package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ThingMapper;
import com.example.mapper.ThingWishMapper;
import com.example.service.ThingWishService;
import com.example.entity.Thing;
import com.example.entity.ThingWish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class ThingWishServiceImpl extends ServiceImpl<ThingWishMapper, ThingWish> implements ThingWishService {
    @Autowired
    ThingWishMapper mapper;

    @Autowired
    ThingMapper thingMapper;

    @Override
    public List<Map> getThingWishList(String userId) {
        return mapper.getThingWishList(userId);
    }

    @Override
    public void createThingWish(ThingWish thingWish) {
        mapper.insert(thingWish);;
    }

    @Override
    public void deleteThingWish(String id) {
        mapper.deleteById(id);
    }

    @Override
    public ThingWish getThingWish(String userId, String thingId) {
        QueryWrapper<ThingWish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("thing_id", thingId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean addWish(ThingWish thingWish) {
        // 检查是否已添加
        if (getThingWish(thingWish.getUserId(), thingWish.getThingId()) != null) {
            return false;
        }
        
        // 创建愿望
        createThingWish(thingWish);
        
        // 更新心愿计数
        Thing thing = thingMapper.selectById(thingWish.getThingId());
        if (thing != null) {
            thing.setWishCount(String.valueOf(Integer.parseInt(thing.getWishCount()) + 1));
            thingMapper.updateById(thing);
        }
        return true;
    }
}
