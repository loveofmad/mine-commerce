package com.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.mapper.BannerMapper;
import com.mine.model.entity.Banner;
import com.mine.service.BannerService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public IPage<Banner> listBanners(int pageNum, int pageSize) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Banner::getSort);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<Banner> listActiveBanners() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1)
               .orderByAsc(Banner::getSort);
        return list(wrapper);
    }

    @Override
    public Banner getBannerById(Long id) {
        return getById(id);
    }

    @Override
    public boolean addBanner(Banner banner) {
        banner.setCreateTime(LocalDateTime.now());
        banner.setUpdateTime(LocalDateTime.now());
        banner.setDeleted(0);
        return save(banner);
    }

    @Override
    public boolean updateBanner(Banner banner) {
        banner.setUpdateTime(LocalDateTime.now());
        return updateById(banner);
    }

    @Override
    public boolean deleteBanner(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setStatus(status);
        banner.setUpdateTime(LocalDateTime.now());
        return updateById(banner);
    }
}
