package com.example.service;


import com.example.entity.Address;

import java.util.List;

public interface AddressService {
    List<Address> getAddressList(Long userId);
    void createAddress(Address address);
    void deleteAddress(String id);

    void updateAddress(Address address);
    
    // 设置默认地址
    void setDefaultAddress(Long userId, Long addressId);
}
