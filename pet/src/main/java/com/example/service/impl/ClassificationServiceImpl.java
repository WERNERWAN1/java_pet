package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.ClassificationMapper;
import com.example.entity.Classification;
import com.example.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, Classification> implements ClassificationService {

    @Autowired
    ClassificationMapper mapper;

    @Override
    public List<Classification> getClassificationList(String keyword) {
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("title", keyword);
        }
        return mapper.selectList(queryWrapper);
    }

    @Override
    public void createClassification(Classification classification) {
        System.out.println(classification);
        classification.setCreateTime(new Date());
        mapper.insert(classification);
    }

    @Override
    public void deleteClassification(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateClassification(Classification classification) {
        // 审计字段由自动填充处理，忽略前端回传的旧值，确保修改人/修改时间被刷新
        classification.setCreateBy(null);
        classification.setCreateByName(null);
        classification.setCreateTime(null);
        classification.setUpdateBy(null);
        classification.setUpdateByName(null);
        classification.setUpdateTime(new Date());
        mapper.updateById(classification);
    }
}
