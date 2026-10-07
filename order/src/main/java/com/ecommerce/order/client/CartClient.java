package com.ecommerce.order.client;

import com.ecommerce.order.dto.CartItemDTO;

import java.util.List;

public interface CartClient {
    List<CartItemDTO> fetchCartItems(Long userId);

    void clearCart(Long userId);
}
