package com.example.demo.service.Interface;

import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductResponsePublic;
import com.example.demo.response.ApiResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ApiResponse<List<ProductResponsePublic>> getAllProducts();
    ApiResponse<List<ProductResponse>> getAllProductForAdmin();
    ApiResponse<ProductResponse> createProduct(CreateProductRequest request);
    ApiResponse<ProductResponsePublic> getProductById(UUID id);
    ApiResponse<ProductResponse> updateProductById(UUID id, CreateProductRequest request);
    ApiResponse<String> deleteProductById(UUID id);
}
