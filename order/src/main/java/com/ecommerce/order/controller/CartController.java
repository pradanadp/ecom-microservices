package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.CartItemResponse;
import com.ecommerce.order.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addToCart(@RequestHeader("X-User-ID") Long userId,
                                          @Valid @RequestBody CartItemRequest request) {
        cartService.addToCart(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-ID") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(@RequestHeader("X-User-ID") Long userId,
                                               @PathVariable Long productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(@RequestHeader("X-User-ID") Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }
}
