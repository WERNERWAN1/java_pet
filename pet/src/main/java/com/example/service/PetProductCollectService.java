package com.example.service;


import com.example.entity.PetProductCollect;

import java.util.List;
import java.util.Map;

public interface PetProductCollectService {
    List<Map> getPetProductCollectList(String userId);
    void createPetProductCollect(PetProductCollect petProductCollect);
    void deletePetProductCollect(String id);
    PetProductCollect getPetProductCollect(Long userId, Long productId);
    
    // 添加收藏
    boolean addCollect(PetProductCollect petProductCollect);
}
