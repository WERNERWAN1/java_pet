package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Address;
import com.example.mapper.AddressMapper;
import com.example.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    @Autowired
    AddressMapper mapper;

    @Override
    public List<Address> getAddressList(String userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderBy(true, false, "create_time");
        return mapper.selectList(queryWrapper);
    }

    @Override
    public void createAddress(Address address) {
        // 如果设置为默认地址，则先取消其他默认地址
        if ("1".equals(address.getDef())) {
            setDefaultAddress(address.getUserId(), address.getId().toString());
        }
        address.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(address);
    }

    @Override
    public void deleteAddress(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateAddress(Address address) {
        // 如果设置为默认地址，则先取消其他默认地址
        if ("1".equals(address.getDef())) {
            setDefaultAddress(address.getUserId(), address.getId().toString());
        }
        mapper.updateById(address);
    }
    
    @Override
    public void setDefaultAddress(String userId, String addressId) {
        // 将用户的所有地址设置为非默认
        List<Address> addresses = getAddressList(userId);
        for (Address address : addresses) {
            if (!address.getId().equals(addressId)) {
                address.setDef("0");
                mapper.updateById(address);
            }
        }
    }
}
