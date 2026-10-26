package com.example.controller;

import com.example.common.APIResponse;
import com.example.common.ResponeCode;
import com.example.entity.Address;
import com.example.service.AddressService;
import com.example.service.BatchDeleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
/**
 * @author wanchun
 * @email email
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    private final static Logger logger = LoggerFactory.getLogger(AddressController.class);

    @Autowired
    AddressService service;
    
    @Autowired
    BatchDeleteService batchDeleteService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(Long userId){
        List<Address> list =  service.getAddressList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Address address) throws IOException {
        // Service内部处理默认地址逻辑
        service.createAddress(address);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        batchDeleteService.batchDelete(ids, service::deleteAddress);
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Address address) throws IOException {
        // Service内部处理默认地址逻辑
        service.updateAddress(address);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

}
