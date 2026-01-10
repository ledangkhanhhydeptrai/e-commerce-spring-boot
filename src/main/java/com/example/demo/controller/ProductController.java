package com.example.demo.controller;


import com.example.demo.dto.request.CreateProductRequest;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.ProductResponsePublic;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Product", description = "Dành cho user và admin")
public class ProductController {
    @Autowired
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/public/product")
    @Operation(description = "Dành cho admin và user")
    public ResponseEntity<ApiResponse<List<ProductResponsePublic>>> getAllProduct() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/product/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Dành cho admin")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProductAdmin() {
        return ResponseEntity.ok(productService.getAllProductForAdmin());
    }

    @PostMapping(
            value = "/product/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Tạo sản phẩm (multipart)")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @ModelAttribute @RequestBody CreateProductRequest request
    ) {
        return ResponseEntity.ok(productService.createProduct(request, request.getFile()));
    }


    @GetMapping("/public/product/{id}")
    @Operation(description = "Dành cho admin và user")
    public ResponseEntity<ApiResponse<ProductResponsePublic>> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/product/{id}")
    @Operation(description = "Dành cho admin")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductAdminById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductAdminById(id));
    }

    @PutMapping(value = "/product/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Dành cho admin")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductById(@PathVariable UUID id, @ModelAttribute @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.updateProductById(id, request, request.getFile()));
    }

    @DeleteMapping("/product/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(description = "Dành cho admin")
    public ResponseEntity<ApiResponse<String>> deleteProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }
}
