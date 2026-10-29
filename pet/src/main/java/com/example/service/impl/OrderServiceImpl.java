package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Goods;
import com.example.entity.Order;
import com.example.entity.PetProduct;
import com.example.service.OrderService;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrderMapper;
import com.example.mapper.PetProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper mapper;
    
    @Autowired
    PetProductMapper petProductMapper;

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public List<Order> getOrderList() {
        return mapper.getList();
    }

    @Override
    public void createOrder(Order order, Integer orderType) {
        int count = (order.getCount() != null) ? order.getCount() : 1;
        int type = (orderType != null) ? orderType : Order.PetProductOrder;
        BigDecimal unitPrice;

        if (type == Order.GoodsOrder) {
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
            int rows = goodsMapper.decrementStock(goods.getId(), count);
            if (rows == 0) {
                throw new RuntimeException("库存不足或已被其他订单占用，请刷新后重试");
            }
            unitPrice = goods.getPrice();
        } else {
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
            int rows = petProductMapper.decrementStock(product.getId(), count);
            if (rows == 0) {
                throw new RuntimeException("库存不足或已被其他订单占用，请刷新后重试");
            }
            unitPrice = product.getPrice();
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
        order.setStatus(1);
        // 类型数字化后实体直接映射 order_type 列，随 insert 一次写入，无需再回写
        order.setOrderType(type);
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
        Order existing = mapper.selectById(orderId);
        if (existing == null) {
            throw new RuntimeException("订单不存在");
        }
        if (existing.getStatus() != null && existing.getStatus() == 7) {
            throw new RuntimeException("订单已取消，不可重复操作");
        }
        int count = (existing.getCount() != null) ? existing.getCount() : 0;
        if (count > 0 && existing.getProductId() != null) {
            if (existing.getOrderType() != null && existing.getOrderType() == Order.GoodsOrder) {
                goodsMapper.incrementStock(existing.getProductId(), count);
            } else {
                petProductMapper.incrementStock(existing.getProductId(), count);
            }
        }
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(7);
        mapper.updateById(order);
    }
}