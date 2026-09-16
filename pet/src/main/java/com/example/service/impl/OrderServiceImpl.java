package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Order;
import com.example.entity.Thing;
import com.example.service.OrderService;
import com.example.mapper.OrderMapper;
import com.example.mapper.ThingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper mapper;
    
    @Autowired
    ThingMapper thingMapper;

    @Override
    public List<Order> getOrderList() {
        return mapper.getList();
    }

    @Override
    public void createOrder(Order order) {
        // 校验库存
        Thing thing = thingMapper.selectById(order.getThingId());
        if (thing == null) {
            throw new RuntimeException("商品不存在");
        }
        if (thing.getStock() == null || thing.getStock() <= 0) {
            throw new RuntimeException("商品已售罄");
        }
        int count = (order.getCount() != null && !order.getCount().isEmpty()) ? Integer.parseInt(order.getCount()) : 1;
        if (thing.getStock() < count) {
            throw new RuntimeException("库存不足，当前库存：" + thing.getStock());
        }
        
        // 扣减库存
        thing.setStock(thing.getStock() - count);
        thingMapper.updateById(thing);
        
        // 创建订单
        long ct = System.currentTimeMillis();
        order.setOrderTime(String.valueOf(ct));
        order.setOrderNumber(String.valueOf(ct));
        order.setStatus("1");
        mapper.insert(order);
    }

    @Override
    public void deleteOrder(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateOrder(Order order) {
        mapper.updateById(order);
    }

    @Override
    public List<Order> getUserOrderList(String userId, String status) {
        return mapper.getUserOrderList(userId, status);
    }
    
    @Override
    public void cancelOrder(Long orderId) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus("7"); // 7=取消
        mapper.updateById(order);
    }
}
