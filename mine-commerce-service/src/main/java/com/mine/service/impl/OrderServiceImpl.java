package com.mine.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.common.exception.BusinessException;
import com.mine.mapper.OrderItemMapper;
import com.mine.mapper.OrderMapper;
import com.mine.model.entity.Order;
import com.mine.model.entity.OrderItem;
import com.mine.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public Order createOrder(Long userId, Long addressId, List<OrderItem> items, Long couponId,
                            String receiverName, String receiverPhone, String receiverAddress) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            item.setTotalAmount(itemTotal);
            totalAmount = totalAmount.add(itemTotal);
        }

        Order order = new Order();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setDeleted(0);
        save(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            orderItemMapper.insert(item);
        }

        return order;
    }

    @Override
    public Order getOrderDetail(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    @Override
    public IPage<Order> listOrdersByUserId(Long userId, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public IPage<Order> listOrders(int pageNum, int pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Order::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public boolean cancelOrder(Long id) {
        Order order = getOrderDetail(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许取消");
        }
        Order update = new Order();
        update.setId(id);
        update.setStatus(4);
        update.setUpdateTime(LocalDateTime.now());
        return updateById(update);
    }

    @Override
    public boolean payOrder(Long id) {
        Order order = getOrderDetail(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许支付");
        }
        Order update = new Order();
        update.setId(id);
        update.setStatus(1);
        update.setPayTime(LocalDateTime.now());
        update.setUpdateTime(LocalDateTime.now());
        return updateById(update);
    }

    @Override
    public boolean deliverOrder(Long id) {
        Order order = getOrderDetail(id);
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不允许发货");
        }
        Order update = new Order();
        update.setId(id);
        update.setStatus(2);
        update.setDeliverTime(LocalDateTime.now());
        update.setUpdateTime(LocalDateTime.now());
        return updateById(update);
    }

    @Override
    public boolean confirmReceive(Long id) {
        Order order = getOrderDetail(id);
        if (order.getStatus() != 2) {
            throw new BusinessException("订单状态不允许确认收货");
        }
        Order update = new Order();
        update.setId(id);
        update.setStatus(3);
        update.setReceiveTime(LocalDateTime.now());
        update.setUpdateTime(LocalDateTime.now());
        return updateById(update);
    }

    @Override
    public boolean deleteOrder(Long id) {
        return removeById(id);
    }

    @Override
    public List<OrderItem> listOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return orderItemMapper.selectList(wrapper);
    }
}
