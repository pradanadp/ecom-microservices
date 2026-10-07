package com.ecommerce.order.client;

import com.ecommerce.order.dto.CartItemDTO;
import com.ecommerce.order.exception.CartServiceUnavailableException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartClientStub implements CartClient {

    private static final String MESSAGE =
            "Cart service is not implemented yet. Wire CartClient to the cart service over HTTP before creating orders.";

    @Override
    public List<CartItemDTO> fetchCartItems(Long userId) {
        throw new CartServiceUnavailableException(MESSAGE);
    }

    @Override
    public void clearCart(Long userId) {
        throw new CartServiceUnavailableException(MESSAGE);
    }
}
