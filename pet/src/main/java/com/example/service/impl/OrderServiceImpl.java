package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Goods;
import com.example.entity.Order;
import com.example.entity.Thing;
import com.example.service.OrderService;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrderMapper;
import com.example.mapper.ThingMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper mapper;
    
    @Autowired
    ThingMapper thingMapper;

    @Autowired
    GoodsMapper goodsMapper;

    @Autowired
    SqlSession sqlSession;

    @Override
    public List<Order> getOrderList() {
        return mapper.getList();
    }

    @Override
    public void createOrder(Order order, String orderType) {
        int count = (order.getCount() != null && !order.getCount().isEmpty()) ? Integer.parseInt(order.getCount()) : 1;
        String type = (orderType != null && !orderType.isEmpty()) ? orderType : "thing";

        if ("goods".equals(type)) {
            // 宠物用品订单 - 查 goods 表
            Goods goods = goodsMapper.selectById(order.getThingId());
            if (goods == null) {
                throw new RuntimeException("商品不存在");
            }
            if (goods.getNum() == null || goods.getNum() <= 0) {
                throw new RuntimeException("商品已售罄");
            }
            if (goods.getNum() < count) {
                throw new RuntimeException("库存不足，当前库存：" + goods.getNum());
            }
            goods.setNum(goods.getNum() - count);
            goodsMapper.updateById(goods);
        } else {
            // 宠物商品订单 - 查 b_thing 表
            Thing thing = thingMapper.selectById(order.getThingId());
            if (thing == null) {
                throw new RuntimeException("商品不存在");
            }
            if (thing.getStock() == null || thing.getStock() <= 0) {
                throw new RuntimeException("商品已售罄");
            }
            if (thing.getStock() < count) {
                throw new RuntimeException("库存不足，当前库存：" + thing.getStock());
            }
            thing.setStock(thing.getStock() - count);
            thingMapper.updateById(thing);
        }

        // 创建订单
        long ct = System.currentTimeMillis();
        order.setOrderTime(String.valueOf(ct));
        order.setOrderNumber(String.valueOf(ct));
        order.setStatus("1");
        mapper.insert(order);

        // 更新订单类型（Order实体无orderType字段，直接执行SQL）
        Map<String, Object> params = new HashMap<>();
        params.put("id", order.getId());
        params.put("orderType", type);
        sqlSession.update("com.example.mapper.OrderMapper.updateOrderType", params);
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