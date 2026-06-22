package com.mine.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mine.common.result.Result;
import com.mine.model.entity.Sku;
import com.mine.model.entity.Spu;
import com.mine.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "小程序商品接口")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ApiOperation("分页查询商品列表")
    @GetMapping("/list")
    public Result<IPage<Spu>> listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(productService.listSpuByPage(keyword, categoryId, 1, null, null, pageNum, pageSize));
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/{id}")
    public Result<Spu> getProduct(@PathVariable Long id) {
        return Result.success(productService.getSpuDetail(id));
    }

    @ApiOperation("获取商品SKU列表")
    @GetMapping("/{spuId}/sku")
    public Result<List<Sku>> listSkus(@PathVariable Long spuId) {
        return Result.success(productService.listSkuBySpuId(spuId));
    }
}
