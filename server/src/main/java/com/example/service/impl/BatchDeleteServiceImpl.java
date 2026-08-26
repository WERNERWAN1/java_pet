package com.example.service.impl;

import com.example.service.BatchDeleteService;
import org.springframework.stereotype.Service;

/**
 * 批量删除服务实现类
 */
@Service
public class BatchDeleteServiceImpl implements BatchDeleteService {
    
    @Override
    public void batchDelete(String ids, DeleteFunction deleteFunction) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        String[] arr = ids.split(",");
        for (String id : arr) {
            if (id != null && !id.trim().isEmpty()) {
                deleteFunction.delete(id.trim());
            }
        }
    }
}