package com.mine.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mine.common.exception.BusinessException;
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
        if (userId == null || userId <= 0) throw new BusinessException("用户ID无效");
        return Result.success(orderService.createOrder(userId, addressId, items, couponId, receiverName, receiverPhone, receiverAddress, remark));
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id, @RequestParam Long userId) {
        Order order = orderService.getOrderDetail(id);
        if (!order.getUserId().equals(userId)) throw new BusinessException("无权查看该订单");
        return Result.success(order);
    }

    @ApiOperation("分页查询用户订单")
    @GetMapping("/list/{userId}")
    public Result<IPage<Order>> listOrders(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        if (userId == null || userId <= 0) throw new BusinessException("用户ID无效");
        return Result.success(orderService.listOrdersByUserId(userId, status, pageNum, pageSize));
    }

    @ApiOperation("获取订单商品列表")
    @GetMapping("/{orderId}/items")
    public Result<List<OrderItem>> listOrderItems(@PathVariable Long orderId, @RequestParam Long userId) {
        Order order = orderService.getOrderDetail(orderId);
        if (!order.getUserId().equals(userId)) throw new BusinessException("无权查看该订单");
        return Result.success(orderService.listOrderItems(orderId));
    }

    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable Long id, @RequestParam Long userId) {
        Order order = orderService.getOrderDetail(id);
        if (!order.getUserId().equals(userId)) throw new BusinessException("无权操作该订单");
        return Result.success(orderService.cancelOrder(id));
    }

    @ApiOperation("支付订单")
    @PutMapping("/{id}/pay")
    public Result<Boolean> payOrder(@PathVariable Long id, @RequestParam Long userId) {
        Order order = orderService.getOrderDetail(id);
        if (!order.getUserId().equals(userId)) throw new BusinessException("无权操作该订单");
        return Result.success(orderService.payOrder(id));
    }

    @ApiOperation("确认收货")
    @PutMapping("/{id}/confirm")
    public Result<Boolean> confirmReceive(@PathVariable Long id, @RequestParam Long userId) {
        Order order = orderService.getOrderDetail(id);
        if (!order.getUserId().equals(userId)) throw new BusinessException("无权操作该订单");
        return Result.success(orderService.confirmReceive(id));
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteOrder(@PathVariable Long id, @RequestParam Long userId) {
        Order order = orderService.getOrderDetail(id);
        if (!order.getUserId().equals(userId)) throw new BusinessException("无权操作该订单");
        return Result.success(orderService.deleteOrder(id));
    }
}
