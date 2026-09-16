package com.example.service;

import java.util.Map;

/**
 * 概览服务接口
 */
public interface OverviewService {
    
    /**
     * 获取系统信息
     * @return 系统信息
     */
    Map<String, String> getSystemInfo();
    
    /**
     * 获取统计数据
     * @return 统计数据
     */
    Map<String, Object> getStatistics();
}