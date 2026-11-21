package com.ecommerce.order.client;

import com.ecommerce.order.dto.CartItemDTO;
import com.ecommerce.order.exception.CartServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class CartClientHttp implements CartClient {

    private final RestClient restClient;

    public CartClientHttp(@Value("${cart.service.url}") String cartServiceUrl) {
        this.restClient = RestClient.create(cartServiceUrl);
    }

    @Override
    public List<CartItemDTO> fetchCartItems(Long userId) {
        try {
            List<CartItemDTO> items = restClient.get()
                    .uri("/api/cart")
                    .header("X-User-ID", userId.toString())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CartItemDTO>>() {
                    });
            return items == null ? List.of() : items;
        } catch (RestClientException ex) {
            throw new CartServiceUnavailableException(
                    "Cart service is unavailable. Unable to fetch cart for user: " + userId);
        }
    }

    @Override
    public void clearCart(Long userId) {
        try {
            restClient.delete()
                    .uri("/api/cart")
                    .header("X-User-ID", userId.toString())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new CartServiceUnavailableException(
                    "Cart service is unavailable. Unable to clear cart for user: " + userId);
        }
    }
}
