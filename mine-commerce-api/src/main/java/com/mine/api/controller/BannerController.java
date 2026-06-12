package com.mine.api.controller;

import com.mine.common.result.Result;
import com.mine.model.entity.Banner;
import com.mine.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "小程序轮播图接口")
@RestController
@RequestMapping("/api/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @ApiOperation("获取启用的轮播图列表")
    @GetMapping("/list")
    public Result<List<Banner>> listBanners() {
        return Result.success(bannerService.listActiveBanners());
    }
}
