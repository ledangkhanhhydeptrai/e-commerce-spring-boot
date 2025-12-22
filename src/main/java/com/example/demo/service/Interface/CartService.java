package com.example.demo.service.Interface;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.response.ApiResponse;

public interface CartService {
    CartResponse addToCart(AddToCartRequest request);
    ApiResponse<CartResponse> getMyCart();
}
