package com.example.demo.controller;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Dành cho user")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(@RequestBody @Valid AddToCartRequest request) {
        System.out.println("HIT CART ADD API: " + request.productId());
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<String>> deleteCart(@PathVariable UUID id) {
        return ResponseEntity.ok(cartService.deleteCart(id));
    }
}
