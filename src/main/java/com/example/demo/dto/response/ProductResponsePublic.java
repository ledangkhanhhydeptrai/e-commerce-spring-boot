package com.example.demo.dto.response;

import com.example.demo.Enum.StockStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;
@Data
@Builder
public class ProductResponsePublic {
    private UUID id;
    private String name;
    private Double price;
    private StockStatus stockStatus; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
}
