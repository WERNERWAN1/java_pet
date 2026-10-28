package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.PetProductMapper;
import com.example.mapper.PetProductWishMapper;
import com.example.service.PetProductWishService;
import com.example.entity.PetProduct;
import com.example.entity.PetProductWish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class PetProductWishServiceImpl extends ServiceImpl<PetProductWishMapper, PetProductWish> implements PetProductWishService {
    @Autowired
    PetProductWishMapper mapper;

    @Autowired
    PetProductMapper petProductMapper;

    @Override
    public List<Map> getPetProductWishList(String userId) {
        return mapper.getPetProductWishList(userId);
    }

    @Override
    public void createPetProductWish(PetProductWish petProductWish) {
        mapper.insert(petProductWish);
    }

    @Override
    public void deletePetProductWish(String id) {
        mapper.deleteById(id);
    }

    @Override
    public PetProductWish getPetProductWish(Long userId, Long productId) {
        QueryWrapper<PetProductWish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean addWish(PetProductWish petProductWish) {
        // 检查是否已添加
        if (getPetProductWish(petProductWish.getUserId(), petProductWish.getProductId()) != null) {
            return false;
        }
        
        // 创建愿望
        createPetProductWish(petProductWish);
        
        // 更新心愿计数
        PetProduct product = petProductMapper.selectById(petProductWish.getProductId());
        if (product != null) {
            product.setWishCount((product.getWishCount() != null ? product.getWishCount() : 0) + 1);
            petProductMapper.updateById(product);
        }
        return true;
    }
}
