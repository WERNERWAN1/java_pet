package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Foster;
import com.example.mapper.FosterMapper;
import com.example.service.FosterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FosterServiceImpl extends ServiceImpl<FosterMapper, Foster> implements FosterService {

    @Autowired
    FosterMapper fosterMapper;

    @Override
    public List<Map<String, Object>> getFosterList(String progress) {
        return fosterMapper.getFosterList(progress);
    }

    /**
     * 新增寄养预约。
     * 业务约束：寄养天数必须为正整数，天数为空或小于等于 0 时拒绝提交，
     * 防止生成无法计算费用、也无法安排房间的无效预约记录。
     */
    @Override
    public void addFoster(Foster foster) {
        if (foster.getDuration() == null || foster.getDuration() <= 0) {
            throw new RuntimeException("寄养天数必须大于 0");
        }
        fosterMapper.insert(foster);
    }

    @Override
    public void updateFoster(Foster foster) {
        fosterMapper.updateById(foster);
    }

    @Override
    public void deleteFoster(Integer orderId) {
        fosterMapper.deleteById(orderId);
    }
}