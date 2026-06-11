package com.mine.api.controller;

import com.mine.common.result.Result;
import com.mine.model.entity.Cart;
import com.mine.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "小程序购物车接口")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @ApiOperation("获取购物车列表")
    @GetMapping("/{userId}")
    public Result<List<Cart>> listCart(@PathVariable Long userId) {
        return Result.success(cartService.listByUserId(userId));
    }

    @ApiOperation("添加购物车")
    @PostMapping
    public Result<Boolean> addCart(@RequestBody Cart cart) {
        return Result.success(cartService.addToCart(cart));
    }

    @ApiOperation("更新购物车数量")
    @PutMapping("/{id}/quantity")
    public Result<Boolean> updateQuantity(@PathVariable Long id,
                                          @RequestParam Integer quantity) {
        return Result.success(cartService.updateQuantity(id, quantity));
    }

    @ApiOperation("更新选中状态")
    @PutMapping("/{id}/checked")
    public Result<Boolean> updateChecked(@PathVariable Long id,
                                         @RequestParam Integer checked) {
        return Result.success(cartService.updateChecked(id, checked));
    }

    @ApiOperation("全选/取消全选")
    @PutMapping("/{userId}/checkAll")
    public Result<Boolean> checkAll(@PathVariable Long userId,
                                    @RequestParam Integer checked) {
        return Result.success(cartService.checkAll(userId, checked));
    }

    @ApiOperation("删除购物车项")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCartItem(@PathVariable Long id) {
        return Result.success(cartService.deleteCartItem(id));
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/{userId}/clear")
    public Result<Boolean> clearCart(@PathVariable Long userId) {
        return Result.success(cartService.clearCart(userId));
    }
}
