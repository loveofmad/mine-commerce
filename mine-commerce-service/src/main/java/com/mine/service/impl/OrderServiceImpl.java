package com.mine.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.common.exception.BusinessException;
import com.mine.mapper.OrderItemMapper;
import com.mine.mapper.OrderMapper;
import com.mine.mapper.SkuMapper;
import com.mine.mapper.SpuMapper;
import com.mine.model.entity.Order;
import com.mine.model.entity.OrderItem;
import com.mine.model.entity.Sku;
import com.mine.model.entity.Spu;
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

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Override
    @Transactional
    public Order createOrder(Long userId, Long addressId, List<OrderItem> items, Long couponId,
                            String receiverName, String receiverPhone, String receiverAddress, String remark) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            if (item.getSkuId() != null && item.getSkuId() > 0) {
                Sku sku = skuMapper.selectById(item.getSkuId());
                if (sku == null) {
                    throw new BusinessException("商品规格不存在");
                }
                if (sku.getStock() < item.getQuantity()) {
                    throw new BusinessException("商品库存不足: " + sku.getTitle());
                }
                item.setPrice(sku.getPrice());
                item.setSkuImage(sku.getImage());
                item.setSkuTitle(sku.getTitle());
            } else if (item.getSpuId() != null) {
                Spu spu = spuMapper.selectById(item.getSpuId());
                if (spu == null) {
                    throw new BusinessException("商品不存在");
                }
                if (spu.getStock() < item.getQuantity()) {
                    throw new BusinessException("商品库存不足: " + spu.getTitle());
                }
                item.setPrice(spu.getPrice());
                item.setSkuImage(spu.getMainImage());
                item.setSkuTitle(spu.getTitle());
            }
            if (item.getSpuName() == null && item.getSpuId() != null) {
                Spu spu = spuMapper.selectById(item.getSpuId());
                if (spu != null) item.setSpuName(spu.getTitle());
            }
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
        order.setRemark(remark);
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
    @Transactional
    public boolean payOrder(Long id) {
        Order order = getOrderDetail(id);
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许支付");
        }

        List<OrderItem> items = listOrderItems(id);
        for (OrderItem item : items) {
            if (item.getSkuId() != null && item.getSkuId() > 0) {
                int affected = skuMapper.deductStock(item.getSkuId(), item.getQuantity());
                if (affected == 0) {
                    throw new BusinessException("库存不足，支付失败: " + item.getSkuTitle());
                }
                skuMapper.increaseSales(item.getSkuId(), item.getQuantity());
            } else if (item.getSpuId() != null) {
                int affected = spuMapper.deductStock(item.getSpuId(), item.getQuantity());
                if (affected == 0) {
                    throw new BusinessException("库存不足，支付失败: " + item.getSpuName());
                }
                spuMapper.increaseSales(item.getSpuId(), item.getQuantity());
            }
        }

        java.util.Set<Long> updatedSpuIds = new java.util.HashSet<>();
        for (OrderItem item : items) {
            if (item.getSkuId() != null && item.getSkuId() > 0) {
                updatedSpuIds.add(item.getSpuId());
            }
        }
        for (Long spuId : updatedSpuIds) {
            LambdaQueryWrapper<Sku> skuWrapper = new LambdaQueryWrapper<>();
            skuWrapper.eq(Sku::getSpuId, spuId);
            List<Sku> skus = skuMapper.selectList(skuWrapper);
            int totalStock = skus.stream().mapToInt(Sku::getStock).sum();
            int totalSales = skus.stream().mapToInt(Sku::getSales).sum();
            Spu spuUpdate = new Spu();
            spuUpdate.setId(spuId);
            spuUpdate.setStock(totalStock);
            spuUpdate.setSales(totalSales);
            spuUpdate.setUpdateTime(LocalDateTime.now());
            spuMapper.updateById(spuUpdate);
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
