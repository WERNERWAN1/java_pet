package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Goods;

import java.util.List;

public interface GoodsService extends IService<Goods> {
    List<Goods> getGoodsList(String keyword);
    void addGoods(Goods goods);
    void updateGoods(Goods goods);
    void deleteGoods(Integer id);
    Goods getGoodsById(Integer id);
}