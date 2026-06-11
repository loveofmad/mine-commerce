package com.mine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {

    boolean addToCart(Cart cart);

    List<Cart> listByUserId(Long userId);

    boolean updateQuantity(Long id, Integer quantity);

    boolean updateChecked(Long id, Integer checked);

    boolean checkAll(Long userId, Integer checked);

    boolean deleteCartItem(Long id);

    boolean clearCart(Long userId);
}
