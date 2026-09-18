package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Adopt;

import java.util.List;
import java.util.Map;

public interface AdoptService extends IService<Adopt> {
    List<Map<String, Object>> getAdoptList(String breed, String state);
    void addAdopt(Adopt adopt);
    void updateAdopt(Adopt adopt);
    void deleteAdopt(Integer recordId);
}