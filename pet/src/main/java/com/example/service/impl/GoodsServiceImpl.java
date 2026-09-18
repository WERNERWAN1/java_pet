package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Goods;
import com.example.mapper.GoodsMapper;
import com.example.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<Goods> getGoodsList(String keyword) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("name", keyword);
        }
        queryWrapper.orderByDesc("id");
        return goodsMapper.selectList(queryWrapper);
    }

    @Override
    public void addGoods(Goods goods) {
        goodsMapper.insert(goods);
    }

    @Override
    public void updateGoods(Goods goods) {
        goodsMapper.updateById(goods);
    }

    @Override
    public void deleteGoods(Integer id) {
        goodsMapper.deleteById(id);
    }

    @Override
    public Goods getGoodsById(Integer id) {
        return goodsMapper.selectById(id);
    }
}