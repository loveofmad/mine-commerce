package com.mine.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.Order;
import com.mine.model.entity.OrderItem;

import java.util.List;

public interface OrderService extends IService<Order> {

    Order createOrder(Long userId, Long addressId, List<OrderItem> items, Long couponId,
                      String receiverName, String receiverPhone, String receiverAddress, String remark);

    Order getOrderDetail(Long id);

    IPage<Order> listOrdersByUserId(Long userId, Integer status, int pageNum, int pageSize);

    IPage<Order> listOrders(int pageNum, int pageSize);

    boolean cancelOrder(Long id);

    boolean payOrder(Long id);

    boolean deliverOrder(Long id);

    boolean confirmReceive(Long id);

    boolean deleteOrder(Long id);

    List<OrderItem> listOrderItems(Long orderId);
}
