package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.Submit;

import java.util.List;

public interface SubmitService extends IService<Submit> {
    List<Submit> getSubmitList();
    void addSubmit(Submit submit);
    void updateSubmit(Submit submit);
    void deleteSubmit(Integer id);
}