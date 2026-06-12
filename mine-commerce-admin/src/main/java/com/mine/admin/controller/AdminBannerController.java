package com.mine.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mine.common.result.Result;
import com.mine.model.entity.Banner;
import com.mine.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "后台管理轮播图接口")
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @ApiOperation("分页查询轮播图列表")
    @GetMapping("/list")
    public Result<IPage<Banner>> listBanners(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(bannerService.listBanners(pageNum, pageSize));
    }

    @ApiOperation("获取轮播图详情")
    @GetMapping("/{id}")
    public Result<Banner> getBanner(@PathVariable Long id) {
        return Result.success(bannerService.getBannerById(id));
    }

    @ApiOperation("添加轮播图")
    @PostMapping
    public Result<Boolean> addBanner(@RequestBody Banner banner) {
        return Result.success(bannerService.addBanner(banner));
    }

    @ApiOperation("更新轮播图")
    @PutMapping("/{id}")
    public Result<Boolean> updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        return Result.success(bannerService.updateBanner(banner));
    }

    @ApiOperation("删除轮播图")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteBanner(@PathVariable Long id) {
        return Result.success(bannerService.deleteBanner(id));
    }

    @ApiOperation("更新轮播图状态")
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(bannerService.updateStatus(id, status));
    }
}
