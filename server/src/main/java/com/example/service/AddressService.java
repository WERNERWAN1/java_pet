package com.example.service;


import com.example.entity.Address;

import java.util.List;

public interface AddressService {
    List<Address> getAddressList(String userId);
    void createAddress(Address address);
    void deleteAddress(String id);

    void updateAddress(Address address);
    
    // 设置默认地址
    void setDefaultAddress(String userId, String addressId);
}
