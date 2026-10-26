package com.example.service;


import com.example.entity.ThingWish;

import java.util.List;
import java.util.Map;

public interface ThingWishService {
    List<Map> getThingWishList(String userId);
    void createThingWish(ThingWish thingWish);
    void deleteThingWish(String id);

    ThingWish getThingWish(Long userId, Long thingId);
    
    // 添加愿望
    boolean addWish(ThingWish thingWish);
}
