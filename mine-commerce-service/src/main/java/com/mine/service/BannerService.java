package com.mine.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.Banner;

public interface BannerService extends IService<Banner> {
    IPage<Banner> listBanners(int pageNum, int pageSize);
    Banner getBannerById(Long id);
    boolean addBanner(Banner banner);
    boolean updateBanner(Banner banner);
    boolean deleteBanner(Long id);
    boolean updateStatus(Long id, Integer status);
}
