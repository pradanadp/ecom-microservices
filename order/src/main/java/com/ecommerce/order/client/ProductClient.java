package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductDTO;

public interface ProductClient {
    ProductDTO fetchProduct(Long productId);
}
