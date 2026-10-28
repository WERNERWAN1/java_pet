package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.AdoptablePet;

import java.util.List;

public interface AdoptablePetService extends IService<AdoptablePet> {
    List<AdoptablePet> getAdoptablePetList(String keyword);
    void addAdoptablePet(AdoptablePet adoptablePet);
    void updateAdoptablePet(AdoptablePet adoptablePet);
    void deleteAdoptablePet(Integer pid);
    AdoptablePet getAdoptablePetById(Integer pid);
}
