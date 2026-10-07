package com.ecommerce.order.service;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.dto.CartItemDTO;
import com.ecommerce.order.dto.OrderItemDTO;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.exception.EmptyCartException;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartClient cartClient;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(Long userId) {
        List<CartItemDTO> cartItems = cartClient.fetchCartItems(userId);
        validateCartNotEmpty(cartItems);

        Order savedOrder = orderRepository.save(buildOrder(userId, cartItems));
        cartClient.clearCart(userId);

        return mapToOrderResponse(savedOrder);
    }

    private void validateCartNotEmpty(List<CartItemDTO> cartItems) {
        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot create order: cart is empty");
        }
    }

    private Order buildOrder(Long userId, List<CartItemDTO> cartItems) {
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(calculateTotal(cartItems))
                .build();
        order.setItems(toOrderItems(cartItems, order));
        return order;
    }

    private BigDecimal calculateTotal(List<CartItemDTO> cartItems) {
        return cartItems.stream()
                .map(this::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal lineTotal(CartItemDTO item) {
        return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    private List<OrderItem> toOrderItems(List<CartItemDTO> cartItems, Order order) {
        return cartItems.stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .price(lineTotal(item))
                        .order(order)
                        .build())
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(this::toOrderItemDTO)
                        .toList(),
                order.getCreatedAt()
        );
    }

    private OrderItemDTO toOrderItemDTO(OrderItem item) {
        return new OrderItemDTO(
                item.getId(),
                item.getProductId(),
                item.getQuantity(),
                item.getPrice(),
                item.getPrice()
        );
    }
}
