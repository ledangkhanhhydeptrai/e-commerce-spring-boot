package com.example.demo.service.Interface;

import com.example.demo.dto.request.CreateOrderRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.response.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    ApiResponse<List<OrderResponse>> getAllOrders();
    ApiResponse<OrderResponse> getOrderById(UUID id);
    ApiResponse<OrderResponse> addOrder();
}
