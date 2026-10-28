package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.PetProduct;
import com.example.entity.PetProductTag;
import com.example.mapper.PetProductMapper;
import com.example.mapper.PetProductTagMapper;
import com.example.service.PetProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PetProductServiceImpl extends ServiceImpl<PetProductMapper, PetProduct> implements PetProductService {
    @Autowired
    PetProductMapper mapper;

    @Autowired
    PetProductTagMapper petProductTagMapper;

    @Override
    public List<PetProduct> getPetProductList(String keyword, String sort, String c, String tag) {
        QueryWrapper<PetProduct> queryWrapper = new QueryWrapper<>();

        // 搜索
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

        // 排序
        if (StringUtils.isNotBlank(sort)) {
            if (sort.equals("recent")) {
                queryWrapper.orderBy(true, false, "create_time");
            } else if (sort.equals("hot") || sort.equals("recommend")) {
                queryWrapper.orderBy(true, false, "pv");
            }
        }else {
            queryWrapper.orderBy(true, false, "create_time");
        }

        // 根据分类筛选
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            queryWrapper.eq(true, "classification_id", c);
        }

        List<PetProduct> products = mapper.selectList(queryWrapper);

        // tag筛选
        if (StringUtils.isNotBlank(tag)) {
            List<PetProduct> tProducts = new ArrayList<>();
            QueryWrapper<PetProductTag> petProductTagQueryWrapper = new QueryWrapper<>();
            petProductTagQueryWrapper.eq("tag_id", tag);
            List<PetProductTag> petProductTagList = petProductTagMapper.selectList(petProductTagQueryWrapper);
            for (PetProduct product : products) {
                for (PetProductTag petProductTag : petProductTagList) {
                    if (product.getId().equals(petProductTag.getProductId())) {
                        tProducts.add(product);
                    }
                }
            }
            products.clear();
            products.addAll(tProducts);
        }

        // 附加tag
        for (PetProduct product : products) {
            QueryWrapper<PetProductTag> petProductTagQueryWrapper = new QueryWrapper<>();
            petProductTagQueryWrapper.lambda().eq(PetProductTag::getProductId, product.getId());
            List<PetProductTag> petProductTags = petProductTagMapper.selectList(petProductTagQueryWrapper);
            List<Long> tags = petProductTags.stream().map(PetProductTag::getTagId).collect(Collectors.toList());
            product.setTags(tags);
        }
        return products;
    }

    @Override
    public void createPetProduct(PetProduct petProduct) {
        System.out.println(petProduct);
        petProduct.setCreateTime(new Date());

        if (petProduct.getPv() == null) {
            petProduct.setPv(0);
        }
        if (petProduct.getScore() == null) {
            petProduct.setScore(0);
        }
        if (petProduct.getWishCount() == null) {
            petProduct.setWishCount(0);
        }
        if (petProduct.getStock() == null) {
            petProduct.setStock(0);
        }
        mapper.insert(petProduct);
        // 更新tag
        setPetProductTags(petProduct);
    }

    @Override
    public void deletePetProduct(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updatePetProduct(PetProduct petProduct) {
        // 更新tag
        setPetProductTags(petProduct);
        mapper.updateById(petProduct);
    }

    @Override
    public PetProduct getPetProductById(String id) {
        return mapper.selectById(id);
    }

    // 心愿数加1
    @Override
    public void addWishCount(String productId) {
        PetProduct product = mapper.selectById(productId);
        product.setWishCount(product.getWishCount() + 1);
        mapper.updateById(product);
    }

    // 收藏数加1
    @Override
    public void addCollectCount(String productId) {
        PetProduct product = mapper.selectById(productId);
        product.setCollectCount(product.getCollectCount() + 1);
        mapper.updateById(product);
    }

    public void setPetProductTags(PetProduct petProduct) {
        // 删除tag
        Map<String, Object> map = new HashMap<>();
        map.put("product_id", petProduct.getId());
        petProductTagMapper.deleteByMap(map);
        // 新增tag
        if (petProduct.getTags() != null) {
            for (Long tag : petProduct.getTags()) {
                PetProductTag petProductTag = new PetProductTag();
                petProductTag.setProductId(petProduct.getId());
                petProductTag.setTagId(tag);
                petProductTagMapper.insert(petProductTag);
            }
        }
    }

}
