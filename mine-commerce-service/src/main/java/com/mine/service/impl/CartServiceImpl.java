package com.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.common.exception.BusinessException;
import com.mine.mapper.CartMapper;
import com.mine.model.entity.Cart;
import com.mine.service.CartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private static final int MAX_QUANTITY_PER_ITEM = 99;
    private static final int MAX_CART_ITEMS = 50;

    @Override
    public boolean addToCart(Cart cart) {
        if (cart.getQuantity() == null || cart.getQuantity() <= 0) {
            throw new BusinessException("购买数量必须大于0");
        }
        if (cart.getQuantity() > MAX_QUANTITY_PER_ITEM) {
            throw new BusinessException("单件商品最多购买" + MAX_QUANTITY_PER_ITEM + "件");
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, cart.getUserId())
                .eq(Cart::getSkuId, cart.getSkuId());
        List<Cart> existList = list(wrapper);
        if (existList != null && !existList.isEmpty()) {
            Cart exist = existList.get(0);
            int newQty = exist.getQuantity() + cart.getQuantity();
            if (newQty > MAX_QUANTITY_PER_ITEM) {
                throw new BusinessException("单件商品最多购买" + MAX_QUANTITY_PER_ITEM + "件");
            }
            exist.setQuantity(newQty);
            exist.setUpdateTime(LocalDateTime.now());
            return updateById(exist);
        }
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        if (cart.getChecked() == null) {
            cart.setChecked(1);
        }
        return save(cart);
    }

    @Override
    public List<Cart> listByUserId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId).orderByDesc(Cart::getCreateTime);
        return list(wrapper);
    }

    @Override
    public boolean updateQuantity(Long id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("购买数量必须大于0");
        }
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            throw new BusinessException("单件商品最多购买" + MAX_QUANTITY_PER_ITEM + "件");
        }
        Cart cart = new Cart();
        cart.setId(id);
        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());
        return updateById(cart);
    }

    @Override
    public boolean updateChecked(Long id, Integer checked) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setChecked(checked);
        cart.setUpdateTime(LocalDateTime.now());
        return updateById(cart);
    }

    @Override
    public boolean checkAll(Long userId, Integer checked) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        Cart update = new Cart();
        update.setChecked(checked);
        update.setUpdateTime(LocalDateTime.now());
        return update(update, wrapper);
    }

    @Override
    public boolean deleteCartItem(Long id) {
        return removeById(id);
    }

    @Override
    public boolean clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return remove(wrapper);
    }
}
