package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Submit;
import com.example.mapper.SubmitMapper;
import com.example.service.SubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        if (submit.getStatus() == null) {
            submit.setStatus(1);
        }
        // 上报时间以服务端时间为准，前端未传时自动补当前时间
        if (submit.getTime() == null) {
            submit.setTime(new Date());
        }
        submitMapper.insert(submit);
    }

    @Override
    public void updateSubmit(Submit submit) {
        if (submit.getStatus() != null && submit.getId() != null) {
            Submit existing = submitMapper.selectById(submit.getId());
            if (existing != null && existing.getStatus() != null) {
                if (existing.getStatus() == 2 && submit.getStatus() == 1) {
                    throw new RuntimeException("已处理的记录不可回退为待处理");
                }
                if (existing.getStatus() == submit.getStatus()) {
                    throw new RuntimeException("状态未变更");
                }
            }
        }
        submitMapper.updateById(submit);
    }

    @Override
    public void deleteSubmit(Integer id) {
        submitMapper.deleteById(id);
    }
}
