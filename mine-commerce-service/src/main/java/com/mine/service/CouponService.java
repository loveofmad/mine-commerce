package com.mine.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.Coupon;

import java.util.List;

public interface CouponService extends IService<Coupon> {

    IPage<Coupon> listCoupons(int pageNum, int pageSize);

    List<Coupon> listAvailableCoupons();

    Coupon getCouponById(Long id);

    boolean addCoupon(Coupon coupon);

    boolean updateCoupon(Coupon coupon);

    boolean deleteCoupon(Long id);

    boolean updateCouponStatus(Long id, Integer status);
}
