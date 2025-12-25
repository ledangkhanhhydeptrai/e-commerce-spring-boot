package com.example.demo.dto.request;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateOrderItemRequest(
        UUID productId,
        int quantity
) {
}
