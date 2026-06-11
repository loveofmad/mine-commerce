package com.mine.admin.controller;

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

@Api(tags = "后台管理商品接口")
@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @ApiOperation("分页查询商品列表")
    @GetMapping("/spu/list")
    public Result<IPage<Spu>> listSpu(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(productService.listSpuByPage(keyword, categoryId, pageNum, pageSize));
    }

    @ApiOperation("获取商品详情")
    @GetMapping("/spu/{id}")
    public Result<Spu> getSpu(@PathVariable Long id) {
        return Result.success(productService.getSpuDetail(id));
    }

    @ApiOperation("添加商品")
    @PostMapping("/spu")
    public Result<Boolean> addSpu(@RequestBody Spu spu) {
        return Result.success(productService.addSpu(spu));
    }

    @ApiOperation("更新商品")
    @PutMapping("/spu")
    public Result<Boolean> updateSpu(@RequestBody Spu spu) {
        return Result.success(productService.updateSpu(spu));
    }

    @ApiOperation("删除商品")
    @DeleteMapping("/spu/{id}")
    public Result<Boolean> deleteSpu(@PathVariable Long id) {
        return Result.success(productService.deleteSpu(id));
    }

    @ApiOperation("更新商品状态")
    @PutMapping("/spu/{id}/status")
    public Result<Boolean> updateSpuStatus(@PathVariable Long id,
                                           @RequestParam Integer status) {
        return Result.success(productService.updateSpuStatus(id, status));
    }

    @ApiOperation("获取SKU列表")
    @GetMapping("/sku/{spuId}")
    public Result<List<Sku>> listSkus(@PathVariable Long spuId) {
        return Result.success(productService.listSkuBySpuId(spuId));
    }

    @ApiOperation("添加SKU")
    @PostMapping("/sku")
    public Result<Boolean> addSku(@RequestBody Sku sku) {
        return Result.success(productService.addSku(sku));
    }

    @ApiOperation("更新SKU")
    @PutMapping("/sku")
    public Result<Boolean> updateSku(@RequestBody Sku sku) {
        return Result.success(productService.updateSku(sku));
    }

    @ApiOperation("删除SKU")
    @DeleteMapping("/sku/{id}")
    public Result<Boolean> deleteSku(@PathVariable Long id) {
        return Result.success(productService.deleteSku(id));
    }

    @ApiOperation("更新SKU状态")
    @PutMapping("/sku/{id}/status")
    public Result<Boolean> updateSkuStatus(@PathVariable Long id,
                                           @RequestParam Integer status) {
        return Result.success(productService.updateSkuStatus(id, status));
    }
}
