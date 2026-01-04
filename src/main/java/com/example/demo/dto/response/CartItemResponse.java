package com.example.demo.dto.response;

import java.util.UUID;

public record CartItemResponse(
        UUID cartItemId,
        UUID productId,
        String productName,
        double price,
        int quantity,
        String image
) {
}
