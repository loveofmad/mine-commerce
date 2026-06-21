package com.mine.api.controller;

import com.mine.common.exception.BusinessException;
import com.mine.common.result.Result;
import com.mine.model.entity.Address;
import com.mine.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "小程序收货地址接口")
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @ApiOperation("获取地址列表")
    @GetMapping("/list")
    public Result<List<Address>> listAddresses(@RequestParam Long userId) {
        if (userId == null || userId <= 0) throw new BusinessException("用户ID无效");
        return Result.success(addressService.listByUserId(userId));
    }

    @ApiOperation("添加地址")
    @PostMapping
    public Result<Boolean> addAddress(@RequestBody Address address) {
        if (address.getUserId() == null || address.getUserId() <= 0) throw new BusinessException("用户ID无效");
        return Result.success(addressService.addAddress(address));
    }

    @ApiOperation("更新地址")
    @PutMapping("/{id}")
    public Result<Boolean> updateAddress(@PathVariable Long id, @RequestBody Address address, @RequestParam Long userId) {
        Address existing = addressService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) throw new BusinessException("无权操作该地址");
        address.setId(id);
        return Result.success(addressService.updateAddress(address));
    }

    @ApiOperation("删除地址")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteAddress(@PathVariable Long id, @RequestParam Long userId) {
        Address existing = addressService.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) throw new BusinessException("无权操作该地址");
        return Result.success(addressService.deleteAddress(id));
    }
}
