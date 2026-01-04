package com.example.demo.service.Interface;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.response.ApiResponse;

import java.util.UUID;

public interface CartService {
    ApiResponse<CartResponse> addToCart(AddToCartRequest request);
    ApiResponse<CartResponse> getMyCart();
    ApiResponse<String> deleteCart(UUID id);
    ApiResponse<CartResponse> updateQuantity(UUID cartItemId, int quantity);
}
