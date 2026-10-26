package com.example.service;


import com.example.entity.ThingCollect;

import java.util.List;
import java.util.Map;

public interface ThingCollectService {
    List<Map> getThingCollectList(String userId);
    void createThingCollect(ThingCollect thingCollect);
    void deleteThingCollect(String id);
    ThingCollect getThingCollect(Long userId, Long thingId);
    
    // 添加收藏
    boolean addCollect(ThingCollect thingCollect);
}
