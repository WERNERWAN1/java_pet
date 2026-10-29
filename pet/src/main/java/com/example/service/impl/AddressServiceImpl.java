package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Address;
import com.example.mapper.AddressMapper;
import com.example.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    @Autowired
    AddressMapper mapper;

    @Override
    public List<Address> getAddressList(Long userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderBy(true, false, "create_time");
        return mapper.selectList(queryWrapper);
    }

    @Override
    public void createAddress(Address address) {
        address.setCreateTime(new Date());
        mapper.insert(address);
        if ("1".equals(address.getDef())) {
            setDefaultAddress(address.getUserId(), address.getId());
        }
    }

    @Override
    public void deleteAddress(String id) {
        Address address = mapper.selectById(id);
        mapper.deleteById(id);
        if (address != null && "1".equals(address.getDef())) {
            List<Address> remaining = getAddressList(address.getUserId());
            if (!remaining.isEmpty()) {
                Address earliest = remaining.get(remaining.size() - 1);
                earliest.setDef("1");
                mapper.updateById(earliest);
            }
        }
    }

    @Override
    public void updateAddress(Address address) {
        if ("1".equals(address.getDef())) {
            setDefaultAddress(address.getUserId(), address.getId());
        }
        mapper.updateById(address);
    }
    
    @Override
    public void setDefaultAddress(Long userId, Long addressId) {
        List<Address> addresses = getAddressList(userId);
        for (Address address : addresses) {
            if (!address.getId().equals(addressId)) {
                address.setDef("0");
                mapper.updateById(address);
            }
        }
    }
}
