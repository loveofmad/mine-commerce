package com.mine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {

    List<Address> listByUserId(Long userId);

    Address getAddressById(Long id);

    boolean addAddress(Address address);

    boolean updateAddress(Address address);

    boolean deleteAddress(Long id);

    boolean setDefault(Long id, Long userId);
}
