package com.example.service;

import com.example.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getOrderList();
    void createOrder(Order order, Integer orderType);
    void deleteOrder(String id);
    void updateOrder(Order order);
    List<Order> getUserOrderList(String userId, String status);
    void cancelOrder(Long orderId);
    /**
     * 管理端将待支付订单标记为已支付，并记录支付时间
     */
    void payOrder(Long orderId);
}