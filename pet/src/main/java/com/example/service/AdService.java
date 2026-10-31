package com.example.service;


import com.example.entity.Ad;

import java.util.List;

public interface AdService {
    List<Ad> getAdList();
    /**
     * 前台展示用：仅返回未过失效时间的广告（expire_time 为空视为长期有效）
     */
    List<Ad> getValidAdList();
    void createAd(Ad ad);
    void deleteAd(String id);

    void updateAd(Ad ad);
}
