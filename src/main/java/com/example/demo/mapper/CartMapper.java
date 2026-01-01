package com.example.demo.mapper;

import com.example.demo.dto.response.CartItemResponse;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
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
                        i.getQuantity(),
                        i.getProduct().getFileUrl()
                ))
                .toList();
        double total = items.stream()
                .mapToDouble(i -> i.price() * i.quantity())
                .sum();
        return new CartResponse(cart.getId(), items, total);
    }
}
