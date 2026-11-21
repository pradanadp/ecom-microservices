package com.ecommerce.cart.service;

import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.dto.CartItemRequest;
import com.ecommerce.cart.dto.CartItemResponse;
import com.ecommerce.cart.dto.ProductDTO;
import com.ecommerce.cart.exception.InsufficientStockException;
import com.ecommerce.cart.exception.ResourceNotFoundException;
import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ProductClient productClient;
    private final CartItemRepository cartItemRepository;

    public void addToCart(Long userId, CartItemRequest request) {
        ProductDTO product = productClient.fetchProduct(request.getProductId());
        validateStock(product, request.getQuantity());

        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, product.getId())
                .orElseGet(() -> createCartItem(userId, product.getId()));
        item.setQuantity(increaseQuantity(item, request.getQuantity()));
        item.setPrice(product.getPrice());

        cartItemRepository.save(item);
    }

    private void validateStock(ProductDTO product, int requestedQuantity) {
        if (product.getStockQuantity() == null || product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName()
                            + ". Available: " + product.getStockQuantity());
        }
    }

    private CartItem createCartItem(Long userId, Long productId) {
        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(productId);
        return item;
    }

    private int increaseQuantity(CartItem existing, int requestedQuantity) {
        return (existing.getQuantity() == null ? 0 : existing.getQuantity()) + requestedQuantity;
    }

    public void removeFromCart(Long userId, Long productId) {
        long deletedCount = cartItemRepository.deleteByUserIdAndProductId(userId, productId);
        if (deletedCount == 0) {
            throw new ResourceNotFoundException(
                    "Cart item not found for user: " + userId + " and product: " + productId);
        }
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .map(this::mapToCartItemResponse)
                .toList();
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getPrice()
        );
    }
}
