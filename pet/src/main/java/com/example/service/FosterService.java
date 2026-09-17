package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Foster;

import java.util.List;
import java.util.Map;

public interface FosterService extends IService<Foster> {
    List<Map<String, Object>> getFosterList();
    void addFoster(Foster foster);
    void updateFoster(Foster foster);
    void deleteFoster(Integer orderId);
}