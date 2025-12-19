package com.example.demo.controller;

import com.example.demo.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User")
public class UserController {
    @GetMapping
    @Operation(summary = "Get all users")
    public void getUsers() {
        System.out.println("Get all users");
    }
}
