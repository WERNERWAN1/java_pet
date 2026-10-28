package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Goods;
import com.example.entity.Order;
import com.example.entity.PetProduct;
import com.example.service.OrderService;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrderMapper;
import com.example.mapper.PetProductMapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper mapper;
    
    @Autowired
    PetProductMapper petProductMapper;

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
        int count = (order.getCount() != null) ? order.getCount() : 1;
        String type = (orderType != null && !orderType.isEmpty()) ? orderType : "pet_product";
        BigDecimal unitPrice;

        if ("goods".equals(type)) {
            // 宠物用品订单 - 查 goods 表
            Goods goods = goodsMapper.selectById(order.getProductId());
            if (goods == null) {
                throw new RuntimeException("商品不存在");
            }
            if (goods.getNum() == null || goods.getNum() <= 0) {
                throw new RuntimeException("商品已售罄");
            }
            if (goods.getNum() < count) {
                throw new RuntimeException("库存不足，当前库存：" + goods.getNum());
            }
            unitPrice = goods.getPrice();
            goods.setNum(goods.getNum() - count);
            goodsMapper.updateById(goods);
        } else {
            // 宠物商品订单 - 查 pet_product 表
            PetProduct product = petProductMapper.selectById(order.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            if (product.getStock() == null || product.getStock() <= 0) {
                throw new RuntimeException("商品已售罄");
            }
            if (product.getStock() < count) {
                throw new RuntimeException("库存不足，当前库存：" + product.getStock());
            }
            unitPrice = product.getPrice();
            product.setStock(product.getStock() - count);
            petProductMapper.updateById(product);
        }

        // 金额快照：在下单瞬间把成交单价与成交金额固化到订单，
        // 后续商品调价或下架都不会改写历史订单金额，保证财务数据可追溯。
        if (unitPrice == null) {
            unitPrice = BigDecimal.ZERO;
        }
        order.setUnitPrice(unitPrice);
        order.setAmount(unitPrice.multiply(BigDecimal.valueOf(count)));

        // 创建订单
        Date now = new Date();
        order.setOrderTime(now);
        order.setOrderNumber(String.valueOf(System.currentTimeMillis()));
        order.setStatus("1");
        mapper.insert(order);

        // 更新订单类型
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