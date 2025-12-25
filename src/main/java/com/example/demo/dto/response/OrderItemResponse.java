package com.example.demo.dto.response;

import java.util.UUID;

public record OrderItemResponse(
        UUID productId,
        int quantity,
        Long price
) {
}
