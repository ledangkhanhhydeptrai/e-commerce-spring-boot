package com.example.demo.mapper;

import com.example.demo.Enum.StockStatus;
import com.example.demo.config.ProductStockConfig;
import com.example.demo.dto.response.ProductResponsePublic;
import com.example.demo.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductStockResponse {
    private final ProductStockConfig productStockConfig;

    public ProductStockResponse(ProductStockConfig productStockConfig) {
        this.productStockConfig = productStockConfig;
    }

    public ProductResponsePublic toPublicResponse(Product product) {
        StockStatus stockStatus;
        if (product.getQuantity() <= 0) {
            stockStatus = StockStatus.OUT_OF_STOCK;
        } else if (product.getQuantity() <= productStockConfig.getLowThreshold()) {
            stockStatus = StockStatus.LOW_STOCK;
        } else {
            stockStatus = StockStatus.IN_STOCK;
        }
        return ProductResponsePublic.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockStatus(stockStatus)
                .build();

    }
}
