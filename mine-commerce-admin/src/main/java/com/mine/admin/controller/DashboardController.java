package com.mine.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mine.common.result.Result;
import com.mine.mapper.OrderMapper;
import com.mine.mapper.OrderItemMapper;
import com.mine.mapper.CategoryMapper;
import com.mine.mapper.SpuMapper;
import com.mine.mapper.UserMapper;
import com.mine.model.entity.Order;
import com.mine.model.entity.OrderItem;
import com.mine.model.entity.Spu;
import com.mine.model.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "仪表盘接口")
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CategoryMapper categoryMapper;
    private final SpuMapper spuMapper;
    private final UserMapper userMapper;

    @ApiOperation("获取仪表盘统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        LambdaQueryWrapper<Order> todayOrderWrapper = new LambdaQueryWrapper<>();
        todayOrderWrapper.ge(Order::getCreateTime, todayStart)
                .le(Order::getCreateTime, todayEnd)
                .ne(Order::getStatus, 4);
        long todayOrderCount = orderMapper.selectCount(todayOrderWrapper);
        stats.put("todayOrders", todayOrderCount);

        LambdaQueryWrapper<Order> todayPayWrapper = new LambdaQueryWrapper<>();
        todayPayWrapper.ge(Order::getPayTime, todayStart)
                .le(Order::getPayTime, todayEnd)
                .in(Order::getStatus, 1, 2, 3);
        java.util.List<Order> paidOrders = orderMapper.selectList(todayPayWrapper);
        BigDecimal todaySales = paidOrders.stream()
                .map(Order::getPayAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("todaySales", todaySales);

        long productCount = spuMapper.selectCount(new LambdaQueryWrapper<Spu>()
                .eq(Spu::getStatus, 1));
        stats.put("productCount", productCount);

        long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1));
        stats.put("userCount", userCount);

        return Result.success(stats);
    }

    @ApiOperation("近7天销售趋势")
    @GetMapping("/salesTrend")
    public Result<Map<String, Object>> getSalesTrend() {
        List<String> dates = new ArrayList<>();
        List<BigDecimal> sales = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));

            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Order::getPayTime, dayStart)
                    .le(Order::getPayTime, dayEnd)
                    .in(Order::getStatus, 1, 2, 3);
            java.util.List<Order> dayOrders = orderMapper.selectList(wrapper);

            BigDecimal daySales = dayOrders.stream()
                    .map(Order::getPayAmount)
                    .filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            sales.add(daySales);
            orderCounts.add((long) dayOrders.size());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("sales", sales);
        result.put("orderCounts", orderCounts);
        return Result.success(result);
    }

    @ApiOperation("订单状态分布")
    @GetMapping("/orderStatus")
    public Result<List<Map<String, Object>>> getOrderStatus() {
        String[] statusNames = {"待付款", "已付款", "已发货", "已完成", "已取消"};
        Integer[] statusValues = {0, 1, 2, 3, 4};
        String[] colors = {"#E6A23C", "#409EFF", "#909399", "#67C23A", "#F56C6C"};

        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < statusNames.length; i++) {
            long count = orderMapper.selectCount(new LambdaQueryWrapper<Order>()
                    .eq(Order::getStatus, statusValues[i]));
            Map<String, Object> item = new HashMap<>();
            item.put("name", statusNames[i]);
            item.put("value", count);
            item.put("itemStyle", Map.of("color", colors[i]));
            result.add(item);
        }
        return Result.success(result);
    }

    @ApiOperation("分类销售排行")
    @GetMapping("/categorySales")
    public Result<Map<String, Object>> getCategorySales() {
        LambdaQueryWrapper<Order> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.in(Order::getStatus, 1, 2, 3);
        java.util.List<Order> paidOrders = orderMapper.selectList(paidWrapper);

        if (paidOrders.isEmpty()) {
            return Result.success(Map.of("categories", List.of(), "sales", List.of()));
        }

        List<Long> orderIds = paidOrders.stream().map(Order::getId).collect(Collectors.toList());
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(OrderItem::getOrderId, orderIds);
        java.util.List<OrderItem> items = orderItemMapper.selectList(itemWrapper);

        Map<Long, BigDecimal> categorySales = new LinkedHashMap<>();
        Map<Long, String> categoryNames = new LinkedHashMap<>();
        for (OrderItem item : items) {
            if (item.getSpuId() != null) {
                Spu spu = spuMapper.selectById(item.getSpuId());
                if (spu != null) {
                    Long catId = spu.getCategoryId();
                    categorySales.merge(catId, item.getTotalAmount(), BigDecimal::add);
                    categoryNames.putIfAbsent(catId, getCategoryName(catId));
                }
            }
        }

        List<Map.Entry<Long, BigDecimal>> sorted = categorySales.entrySet().stream()
                .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
                .limit(8)
                .collect(Collectors.toList());

        List<String> categories = new ArrayList<>();
        List<BigDecimal> salesData = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : sorted) {
            categories.add(categoryNames.getOrDefault(entry.getKey(), "未知"));
            salesData.add(entry.getValue());
        }

        return Result.success(Map.of("categories", categories, "sales", salesData));
    }

    private String getCategoryName(Long categoryId) {
        com.mine.model.entity.Category cat = categoryMapper.selectById(categoryId);
        return cat != null ? cat.getName() : "未知";
    }
}
