package com.mine.admin.controller;

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

@Api(tags = "后台管理订单接口")
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @ApiOperation("分页查询订单列表")
    @GetMapping("/list")
    public Result<IPage<Order>> listOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(orderService.listOrders(pageNum, pageSize));
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @ApiOperation("获取订单商品列表")
    @GetMapping("/{orderId}/items")
    public Result<List<OrderItem>> listOrderItems(@PathVariable Long orderId) {
        return Result.success(orderService.listOrderItems(orderId));
    }

    @ApiOperation("发货")
    @PutMapping("/{id}/deliver")
    public Result<Boolean> deliverOrder(@PathVariable Long id) {
        return Result.success(orderService.deliverOrder(id));
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteOrder(@PathVariable Long id) {
        return Result.success(orderService.deleteOrder(id));
    }
}
