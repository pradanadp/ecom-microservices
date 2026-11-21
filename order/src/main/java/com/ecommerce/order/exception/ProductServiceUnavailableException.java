package com.ecommerce.order.exception;

public class ProductServiceUnavailableException extends RuntimeException {
    public ProductServiceUnavailableException(String message) {
        super(message);
    }
}
