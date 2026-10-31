package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Goods;
import com.example.entity.Order;
import com.example.entity.PetProduct;
import com.example.service.OrderService;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrderMapper;
import com.example.mapper.PetProductMapper;
import com.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    UserMapper userMapper;

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
        order.setStatus(1);
        // 类型数字化后实体直接映射 order_type 列，随 insert 一次写入，无需再回写
        order.setOrderType(type);
        mapper.insert(order);
        // 订单号规则 D00+自增ID，需在插入拿到ID后回填，保证唯一
        mapper.fillOrderNumber(order.getId());
        order.setOrderNumber("D00" + order.getId());
    }

    @Override
    public void deleteOrder(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateOrder(Order order) {
        if (order == null || order.getId() == null) {
            throw new RuntimeException("订单ID不能为空");
        }
        Order existing = mapper.selectById(order.getId());
        if (existing == null) {
            throw new RuntimeException("订单不存在");
        }
        if (existing.getStatus() != null && existing.getStatus() == 7) {
            throw new RuntimeException("订单已取消，不可修改");
        }

        int oldCount = (existing.getCount() != null) ? existing.getCount() : 0;
        int newCount = (order.getCount() != null) ? order.getCount() : oldCount;
        if (newCount <= 0) {
            throw new RuntimeException("购买数量必须大于 0");
        }

        BigDecimal unitPrice = (existing.getUnitPrice() != null) ? existing.getUnitPrice() : BigDecimal.ZERO;
        // 未传优惠金额时沿用原值，避免确认收货等只改状态的操作误将优惠清零
        BigDecimal discount = (order.getDiscountAmount() != null) ? order.getDiscountAmount()
                : ((existing.getDiscountAmount() != null) ? existing.getDiscountAmount() : BigDecimal.ZERO);
        if (discount.signum() < 0) {
            throw new RuntimeException("优惠金额不能为负数");
        }
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(newCount));
        if (discount.compareTo(total) > 0) {
            throw new RuntimeException("优惠金额不能超过商品总额");
        }

        // 所有校验通过后再联动库存，避免校验失败时库存被误改：
        // 加购再扣库存，减购归还库存
        adjustStock(existing, newCount - oldCount);

        Order update = new Order();
        update.setId(existing.getId());
        // 状态流转（如用户确认收货）仍由该接口透传，未传则保持原状态
        update.setStatus(order.getStatus());
        update.setCount(newCount);
        update.setDiscountAmount(discount);
        update.setAmount(total.subtract(discount));
        update.setReceiverPhone(order.getReceiverPhone());
        update.setReceiverAddress(order.getReceiverAddress());
        mapper.updateById(update);

        int finalStatus = (order.getStatus() != null) ? order.getStatus()
                : ((existing.getStatus() != null) ? existing.getStatus() : 1);
        // 确认收货（订单完成）后按实付金额发放积分
        awardScoreOnCompleted(existing, finalStatus, update.getAmount());
    }

    /**
     * 订单完成时发放积分：按实付金额 1 元 = 1 积分向下取整。
     * 通过条件更新 award_score 作为闸门，保证同一订单只入账一次。
     */
    private void awardScoreOnCompleted(Order existing, int finalStatus, BigDecimal finalAmount) {
        if (finalStatus != 3) {
            return;
        }
        // 已完成订单再次保存（如修改收货地址）不重复发放
        if (existing.getStatus() != null && existing.getStatus() == 3) {
            return;
        }
        if (existing.getUserId() == null || finalAmount == null) {
            return;
        }
        int points = finalAmount.setScale(0, RoundingMode.DOWN).intValue();
        if (points <= 0) {
            return;
        }
        int rows = mapper.markScoreAwarded(existing.getId(), points);
        if (rows > 0) {
            userMapper.addScore(existing.getUserId(), points);
        }
    }

    /**
     * 订单购买数量变更时联动库存：diff > 0 扣减库存，diff < 0 归还库存
     */
    private void adjustStock(Order existing, int diff) {
        if (diff == 0 || existing.getProductId() == null) {
            return;
        }
        boolean goodsOrder = existing.getOrderType() != null && existing.getOrderType() == Order.GoodsOrder;
        if (diff > 0) {
            int rows = goodsOrder
                    ? goodsMapper.decrementStock(existing.getProductId(), diff)
                    : petProductMapper.decrementStock(existing.getProductId(), diff);
            if (rows == 0) {
                throw new RuntimeException("库存不足，无法增加购买数量");
            }
        } else {
            int back = -diff;
            if (goodsOrder) {
                goodsMapper.incrementStock(existing.getProductId(), back);
            } else {
                petProductMapper.incrementStock(existing.getProductId(), back);
            }
        }
    }

    @Override
    public List<Order> getUserOrderList(String userId, String status) {
        return mapper.getUserOrderList(userId, status);
    }
    
    @Override
    public void payOrder(Long orderId) {
        if (orderId == null) {
            throw new RuntimeException("订单ID不能为空");
        }
        Order existing = mapper.selectById(orderId);
        if (existing == null) {
            throw new RuntimeException("订单不存在");
        }
        if (existing.getStatus() != null && existing.getStatus() == 7) {
            throw new RuntimeException("订单已取消，不可标记支付");
        }
        // 仅待支付订单可标记支付，避免重复确认或跳过流程
        if (existing.getStatus() == null || existing.getStatus() != 1) {
            throw new RuntimeException("仅待支付订单可标记为已支付");
        }
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(2);
        // 支付时间以操作时刻为准，用于订单详情与后续对账
        order.setPayTime(new Date());
        mapper.updateById(order);
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
        // 已完成订单被取消时扣回已发放积分，保持积分与订单状态一致
        if (existing.getAwardScore() != null && existing.getAwardScore() > 0
                && existing.getUserId() != null && mapper.clearScoreAwarded(orderId) > 0) {
            userMapper.deductScore(existing.getUserId(), existing.getAwardScore());
        }
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(7);
        mapper.updateById(order);
    }
}