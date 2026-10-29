package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Order;
import com.example.permission.Access;
import com.example.permission.AccessLevel;
import com.example.service.BatchDeleteService;
import com.example.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService service;
    
    @Autowired
    BatchDeleteService batchDeleteService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Order> list =  service.getOrderList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 用户订单
    @RequestMapping(value = "/userOrderList", method = RequestMethod.GET)
    public APIResponse userOrderList(String userId, String status){
        List<Order> list =  service.getUserOrderList(userId, status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Order order, Integer orderType) throws IOException {
        try {
            service.createOrder(order, orderType);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        } catch (RuntimeException e) {
            // 库存不足、商品不存在等业务校验失败，统一返回可读提示而非 500 错误
            logger.warn("下单失败：{}", e.getMessage());
            return new APIResponse(ResponeCode.FAIL, e.getMessage());
        }
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        batchDeleteService.batchDelete(ids, service::deleteOrder);
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Order order) throws IOException {
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse cancelOrder(Long id) throws IOException {
        service.cancelOrder(id);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse cancelUserOrder(Long id) throws IOException {
        service.cancelOrder(id);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }

}