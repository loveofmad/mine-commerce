package com.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.common.exception.BusinessException;
import com.mine.mapper.CouponMapper;
import com.mine.model.entity.Coupon;
import com.mine.service.CouponService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Override
    public IPage<Coupon> listCoupons(int pageNum, int pageSize) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Coupon::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Coupon> listAvailableCoupons() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1)
                .le(Coupon::getStartTime, now)
                .ge(Coupon::getEndTime, now);
        return list(wrapper);
    }

    @Override
    public Coupon getCouponById(Long id) {
        Coupon coupon = getById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        return coupon;
    }

    @Override
    public boolean addCoupon(Coupon coupon) {
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());
        coupon.setDeleted(0);
        if (coupon.getUsed() == null) {
            coupon.setUsed(0);
        }
        return save(coupon);
    }

    @Override
    public boolean updateCoupon(Coupon coupon) {
        coupon.setUpdateTime(LocalDateTime.now());
        return updateById(coupon);
    }

    @Override
    public boolean deleteCoupon(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateCouponStatus(Long id, Integer status) {
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setStatus(status);
        coupon.setUpdateTime(LocalDateTime.now());
        return updateById(coupon);
    }
}
