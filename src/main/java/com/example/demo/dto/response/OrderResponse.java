package com.example.demo.dto.response;

import com.example.demo.Enum.OrderStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Builder
public record OrderResponse(
        UUID id,
        List<OrderItemResponse> items,
        Double totalPrice,
        OrderStatus status,
        LocalDate createdAt,
        LocalDate updatedAt
) {}

