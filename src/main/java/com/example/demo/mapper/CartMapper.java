package com.example.demo.mapper;

import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.response.ApiResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {
    public CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems()
                .stream()
                .map(i -> new CartItemResponse(
                        i.getProduct().getId(),
                        i.getProduct().getName(),
                        i.getProduct().getPrice(),
                        i.getQuantity()
                ))
                .toList();
        double total = items.stream()
                .mapToDouble(i -> i.price() * i.quantity())
                .sum();
        CartResponse cartResponse = new CartResponse(
                cart.getId(),
                items,
                total
        );
        return new CartResponse(cart.getId(), items, total);
    }
}
