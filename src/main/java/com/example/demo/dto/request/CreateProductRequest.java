package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateProductRequest {
    private String name;
    private Long price;
    private int quantity;
}
