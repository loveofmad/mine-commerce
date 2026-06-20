package com.mine.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mine.common.result.Result;
import com.mine.model.entity.Order;
import com.mine.model.entity.OrderItem;
import com.mine.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "小程序订单接口")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping
    public Result<Order> createOrder(@RequestParam Long userId,
                                     @RequestParam Long addressId,
                                     @RequestBody List<OrderItem> items,
                                     @RequestParam(required = false) Long couponId,
                                     @RequestParam(required = false) String receiverName,
                                     @RequestParam(required = false) String receiverPhone,
                                     @RequestParam(required = false) String receiverAddress,
                                     @RequestParam(required = false) String remark) {
        return Result.success(orderService.createOrder(userId, addressId, items, couponId, receiverName, receiverPhone, receiverAddress, remark));
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @ApiOperation("分页查询用户订单")
    @GetMapping("/list/{userId}")
    public Result<IPage<Order>> listOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.listOrdersByUserId(userId, status, pageNum, pageSize));
    }

    @ApiOperation("获取订单商品列表")
    @GetMapping("/{orderId}/items")
    public Result<List<OrderItem>> listOrderItems(@PathVariable Long orderId) {
        return Result.success(orderService.listOrderItems(orderId));
    }

    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Long id) {
        return Result.success(orderService.cancelOrder(id));
    }

    @ApiOperation("支付订单")
    @PutMapping("/{id}/pay")
    public Result<Boolean> payOrder(@PathVariable Long id) {
        return Result.success(orderService.payOrder(id));
    }

    @ApiOperation("确认收货")
    @PutMapping("/{id}/confirm")
    public Result<Boolean> confirmReceive(@PathVariable Long id) {
        return Result.success(orderService.confirmReceive(id));
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteOrder(@PathVariable Long id) {
        return Result.success(orderService.deleteOrder(id));
    }
}
