package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.entity.Order;
import com.example.entity.PetProduct;
import com.example.entity.VisitData;
import com.example.mapper.OrderMapper;
import com.example.mapper.OverviewMapper;
import com.example.mapper.PetProductMapper;
import com.example.service.OverviewService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 概览服务实现类
 */
@Service
public class OverviewServiceImpl implements OverviewService {
    
    @Autowired
    private PetProductMapper petProductMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OverviewMapper overviewMapper;
    
    @Override
    public Map<String, String> getSystemInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("sysName", "后台系统");
        map.put("versionName", "1.0");
        map.put("processor", "2");
        map.put("sysLan", "En");
        map.put("sysZone", "东八区");
        
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        map.put("pf", osmxb.getArch());
        map.put("osName", osmxb.getName());
        map.put("cpuCount", String.valueOf(osmxb.getAvailableProcessors()));
        
        DecimalFormat df = new DecimalFormat("#,##0.0");
        
        //获取CPU
        double cpuLoad = osmxb.getSystemCpuLoad();
        //获取内存
        double totalvirtualMemory = osmxb.getTotalPhysicalMemorySize();
        double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        double value = freePhysicalMemorySize / totalvirtualMemory;
        double percentMemoryLoad = ((1 - value) * 100);
        map.put("cpuLoad", df.format(cpuLoad * 100));
        map.put("memory", df.format(totalvirtualMemory / 1024 / 1024 / 1024));
        map.put("usedMemory", df.format((totalvirtualMemory - freePhysicalMemorySize) / 1024 / 1024 / 1024));
        map.put("percentMemory", df.format(percentMemoryLoad));
        
        map.put("jvmVersion", System.getProperty("java.version"));
        
        return map;
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> map = new HashMap<>();
        
        // 商品总数
        QueryWrapper<PetProduct> queryWrapper = new QueryWrapper<>();
        long spzs = petProductMapper.selectCount(queryWrapper);
        map.put("spzs", spzs);
        
        long now = System.currentTimeMillis();
        long sevenMillis = now - 7 * 24 * 60 * 60 * 1000; // 7天前
        
        // 七日新增
        queryWrapper.ge("create_time", sevenMillis);
        long qrxz = petProductMapper.selectCount(queryWrapper);
        map.put("qrxz", qrxz);
        
        // 未付订单
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "1");
        long wfdd = orderMapper.selectCount(orderQueryWrapper);
        map.put("wfdd", wfdd);
        
        // 未付订单人数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "1");
        orderQueryWrapper.groupBy("user_id");
        map.put("wfddrs", orderMapper.selectList(orderQueryWrapper).size());
        
        // 已付订单
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "2");
        long yfdd = orderMapper.selectCount(orderQueryWrapper);
        map.put("yfdd", yfdd);
        
        // 已付订单人数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "2");
        orderQueryWrapper.groupBy("user_id");
        map.put("yfddrs", orderMapper.selectList(orderQueryWrapper).size());
        
        // 取消订单
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.eq("status", "7");
        long qxdd = orderMapper.selectCount(orderQueryWrapper);
        map.put("qxdd", qxdd);
        
        // 取消订单人数
        orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.select("distinct user_id");
        orderQueryWrapper.eq("status", "7");
        orderQueryWrapper.groupBy("user_id");
        map.put("qxddrs", orderMapper.selectList(orderQueryWrapper).size());
        
        // 热门商品
        List<Object> popularThings = overviewMapper.getPopularThing();
        map.put("popularThings", popularThings);
        
        // 热门分类
        List<Object> popularClassification = overviewMapper.getPopularClassification();
        map.put("popularClassification", popularClassification);
        
        // 网站流量
        List<Object> visitList = new ArrayList<>();
        List<String> sevenList = getSevenDate();
        for(String day: sevenList){
            Map<String, String> visitMap = new HashMap<>();
            visitMap.put("day", day);
            int pv = 0;
            int uv = 0;
            List<VisitData> webVisitData = overviewMapper.getWebVisitData(day);
            for(VisitData visitData: webVisitData) {
                pv += visitData.count;
            }
            uv = webVisitData.size();
            visitMap.put("pv", String.valueOf(pv));
            visitMap.put("uv", String.valueOf(uv));
            visitList.add(visitMap);
        }
        map.put("visitList", visitList);
        
        return map;
    }
    
    private List<String> getSevenDate() {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 0; i < 7; i++) {
            Date date = DateUtils.addDays(new Date(), -i);
            String formatDate = sdf.format(date);
            dateList.add(formatDate);
        }
        Collections.reverse(dateList);
        return dateList;
    }
}