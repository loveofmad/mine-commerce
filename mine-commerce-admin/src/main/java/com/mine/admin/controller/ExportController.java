package com.mine.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mine.common.result.Result;
import com.mine.mapper.OrderMapper;
import com.mine.model.entity.Order;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Api(tags = "数据导出接口")
@RestController
@RequestMapping("/admin/export")
@RequiredArgsConstructor
public class ExportController {

    private final OrderMapper orderMapper;

    @ApiOperation("导出订单数据")
    @GetMapping("/orders")
    public void exportOrders(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", 
                "attachment;filename=orders_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv");

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreateTime);
        List<Order> orders = orderMapper.selectList(wrapper);

        PrintWriter writer = response.getWriter();
        writer.println("订单号,用户ID,收货人,电话,收货地址,总金额,实付金额,状态,创建时间");

        for (Order order : orders) {
            String status = getStatusText(order.getStatus());
            writer.println(String.format("%s,%d,%s,%s,%s,%s,%s,%s,%s",
                    order.getOrderNo(),
                    order.getUserId(),
                    order.getReceiverName() != null ? order.getReceiverName() : "",
                    order.getReceiverPhone() != null ? order.getReceiverPhone() : "",
                    order.getReceiverAddress() != null ? order.getReceiverAddress() : "",
                    order.getTotalAmount(),
                    order.getPayAmount(),
                    status,
                    order.getCreateTime() != null ? order.getCreateTime().toString() : ""
            ));
        }
        writer.flush();
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待付款";
            case 1: return "已付款";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
