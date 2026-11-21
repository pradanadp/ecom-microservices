package com.ecommerce.cart.client;

import com.ecommerce.cart.dto.ProductDTO;

public interface ProductClient {
    ProductDTO fetchProduct(Long productId);
}
