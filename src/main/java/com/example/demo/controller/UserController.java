package com.example.demo.controller;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all user for admin")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser());
    }

}
