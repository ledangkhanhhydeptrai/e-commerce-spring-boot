package com.example.demo.service.Interface;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.response.ApiResponse;

import java.util.List;

public interface UserService {
    ApiResponse<List<UserResponse>> getAllUser();
}
