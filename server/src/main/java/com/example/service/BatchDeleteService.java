package com.example.service;

/**
 * 批量删除服务接口
 */
public interface BatchDeleteService {
    
    /**
     * 批量删除
     * @param ids 逗号分隔的ID字符串
     * @param deleteFunction 删除函数
     */
    void batchDelete(String ids, DeleteFunction deleteFunction);
    
    /**
     * 删除函数接口
     */
    @FunctionalInterface
    interface DeleteFunction {
        void delete(String id);
    }
}