package com.example.service;


import com.example.entity.PetProductWish;

import java.util.List;
import java.util.Map;

public interface PetProductWishService {
    List<Map> getPetProductWishList(String userId);
    void createPetProductWish(PetProductWish petProductWish);
    void deletePetProductWish(String id);

    PetProductWish getPetProductWish(Long userId, Long productId);
    
    // 添加愿望
    boolean addWish(PetProductWish petProductWish);
}
