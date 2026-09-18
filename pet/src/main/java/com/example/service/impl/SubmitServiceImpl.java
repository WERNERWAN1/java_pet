package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Submit;
import com.example.mapper.SubmitMapper;
import com.example.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmitServiceImpl extends ServiceImpl<SubmitMapper, Submit> implements SubmitService {

    @Autowired
    SubmitMapper submitMapper;

    @Override
    public List<Submit> getSubmitList(String keyword) {
        QueryWrapper<Submit> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("name", keyword);
        }
        queryWrapper.orderByDesc("id");
        return submitMapper.selectList(queryWrapper);
    }

    @Override
    public void addSubmit(Submit submit) {
        submitMapper.insert(submit);
    }

    @Override
    public void updateSubmit(Submit submit) {
        submitMapper.updateById(submit);
    }

    @Override
    public void deleteSubmit(Integer id) {
        submitMapper.deleteById(id);
    }
}