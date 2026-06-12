package com.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.common.exception.BusinessException;
import com.mine.mapper.AddressMapper;
import com.mine.model.entity.Address;
import com.mine.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> listByUserId(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).orderByDesc(Address::getIsDefault).orderByDesc(Address::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Address getAddressById(Long id) {
        Address address = getById(id);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }

    @Override
    public boolean addAddress(Address address) {
        // 检查地址是否重复（姓名+手机号+详细地址相同）
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, address.getUserId())
               .eq(Address::getName, address.getName())
               .eq(Address::getPhone, address.getPhone())
               .eq(Address::getDetail, address.getDetail());
        if (count(wrapper) > 0) {
            throw new BusinessException("该地址已存在，请勿重复添加");
        }
        
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        address.setDeleted(0);
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        if (address.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }
        return save(address);
    }

    @Override
    public boolean updateAddress(Address address) {
        address.setUpdateTime(LocalDateTime.now());
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }
        return updateById(address);
    }

    @Override
    public boolean deleteAddress(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean setDefault(Long id, Long userId) {
        clearDefault(userId);
        Address address = new Address();
        address.setId(id);
        address.setIsDefault(1);
        address.setUpdateTime(LocalDateTime.now());
        return updateById(address);
    }

    private void clearDefault(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId).eq(Address::getIsDefault, 1);
        Address update = new Address();
        update.setIsDefault(0);
        update.setUpdateTime(LocalDateTime.now());
        update(update, wrapper);
    }
}
