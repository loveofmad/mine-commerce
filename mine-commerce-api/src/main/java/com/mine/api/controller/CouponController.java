package com.mine.api.controller;

import com.mine.common.result.Result;
import com.mine.model.entity.Coupon;
import com.mine.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "小程序优惠券接口")
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @ApiOperation("获取可用优惠券列表")
    @GetMapping("/available")
    public Result<List<Coupon>> listAvailableCoupons() {
        return Result.success(couponService.listAvailableCoupons());
    }

    @ApiOperation("获取优惠券详情")
    @GetMapping("/{id}")
    public Result<Coupon> getCoupon(@PathVariable Long id) {
        return Result.success(couponService.getCouponById(id));
    }
}
