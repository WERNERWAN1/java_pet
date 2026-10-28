package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.PetProductCollectMapper;
import com.example.mapper.PetProductMapper;
import com.example.service.PetProductCollectService;
import com.example.entity.PetProduct;
import com.example.entity.PetProductCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class PetProductCollectServiceImpl extends ServiceImpl<PetProductCollectMapper, PetProductCollect> implements PetProductCollectService {
    @Autowired
    PetProductCollectMapper mapper;

    @Autowired
    PetProductMapper petProductMapper;

    @Override
    public List<Map> getPetProductCollectList(String userId) {
        return mapper.getPetProductCollectList(userId);
    }

    @Override
    public void createPetProductCollect(PetProductCollect petProductCollect) {
        mapper.insert(petProductCollect);
    }

    @Override
    public void deletePetProductCollect(String id) {
        mapper.deleteById(id);
    }

    @Override
    public PetProductCollect getPetProductCollect(Long userId, Long productId) {
        QueryWrapper<PetProductCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_id", productId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
    
    @Override
    public boolean addCollect(PetProductCollect petProductCollect) {
        // 检查是否已收藏
        if (getPetProductCollect(petProductCollect.getUserId(), petProductCollect.getProductId()) != null) {
            return false;
        }
        
        // 创建收藏
        createPetProductCollect(petProductCollect);
        
        // 更新收藏计数
        PetProduct product = petProductMapper.selectById(petProductCollect.getProductId());
        if (product != null) {
            product.setCollectCount((product.getCollectCount() != null ? product.getCollectCount() : 0) + 1);
            petProductMapper.updateById(product);
        }
        return true;
    }
}
