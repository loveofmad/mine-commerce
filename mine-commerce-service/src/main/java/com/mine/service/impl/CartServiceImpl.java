package com.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.mapper.CartMapper;
import com.mine.model.entity.Cart;
import com.mine.service.CartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Override
    public boolean addToCart(Cart cart) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, cart.getUserId())
                .eq(Cart::getSkuId, cart.getSkuId());
        Cart exist = getOne(wrapper);
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + cart.getQuantity());
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
