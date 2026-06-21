package com.mine.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mine.common.result.Result;
import com.mine.mapper.SkuMapper;
import com.mine.mapper.SpuMapper;
import com.mine.model.entity.Sku;
import com.mine.model.entity.Spu;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "库存预警接口")
@RestController
@RequestMapping("/admin/stock")
@RequiredArgsConstructor
public class StockWarningController {

    private final SkuMapper skuMapper;
    private final SpuMapper spuMapper;

    @ApiOperation("获取库存预警列表")
    @GetMapping("/warning")
    public Result<List<Map<String, Object>>> getStockWarnings(
            @RequestParam(defaultValue = "10") int threshold) {
        List<Map<String, Object>> warnings = new ArrayList<>();

        LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
        skuWrapper.le(Sku::getStock, threshold).gt(Sku::getStock, 0);
        List<Sku> lowStockSkus = skuMapper.selectList(skuWrapper);
        for (Sku sku : lowStockSkus) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "SKU");
            item.put("id", sku.getId());
            item.put("name", sku.getTitle());
            item.put("stock", sku.getStock());
            item.put("threshold", threshold);
            warnings.add(item);
        }

        LambdaQueryWrapper<Spu> spuWrapper = new LambdaQueryWrapper<>();
        spuWrapper.le(Spu::getStock, threshold).gt(Spu::getStock, 0);
        List<Spu> lowStockSpus = spuMapper.selectList(spuWrapper);
        for (Spu spu : lowStockSpus) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "SPU");
            item.put("id", spu.getId());
            item.put("name", spu.getTitle());
            item.put("stock", spu.getStock());
            item.put("threshold", threshold);
            warnings.add(item);
        }

        return Result.success(warnings);
    }

    @ApiOperation("获取库存预警数量")
    @GetMapping("/warning/count")
    public Result<Integer> getStockWarningCount(
            @RequestParam(defaultValue = "10") int threshold) {
        long skuCount = skuMapper.selectCount(
                new LambdaQueryWrapper<Sku>().le(Sku::getStock, threshold).gt(Sku::getStock, 0));
        long spuCount = spuMapper.selectCount(
                new LambdaQueryWrapper<Spu>().le(Spu::getStock, threshold).gt(Spu::getStock, 0));
        return Result.success((int) (skuCount + spuCount));
    }
}
