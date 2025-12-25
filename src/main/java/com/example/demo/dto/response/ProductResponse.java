package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private Long price;
    private int quantity;
}
