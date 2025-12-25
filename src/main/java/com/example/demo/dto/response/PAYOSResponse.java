package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
public record PAYOSResponse(
        String checkoutUrl,
        String paymentLinkId,
        long orderCode) {
}
