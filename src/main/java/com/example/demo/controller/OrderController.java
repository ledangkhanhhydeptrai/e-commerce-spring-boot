package com.example.demo.controller;

import com.example.demo.dto.request.CreateOrderRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@Tag(name = "Order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID id) {
//        return ResponseEntity.ok(orderService.getOrderById(id));
//    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder() {
        return ResponseEntity.ok(orderService.addOrder());
    }
}
