package com.example.service;


import com.example.entity.PetProduct;

import java.util.List;

public interface PetProductService {
    List<PetProduct> getPetProductList(String keyword, String sort, String c, String tag);
    void createPetProduct(PetProduct petProduct);
    void deletePetProduct(String id);

    void updatePetProduct(PetProduct petProduct);

    PetProduct getPetProductById(String id);

    void addWishCount(String productId);

    void addCollectCount(String productId);
}
