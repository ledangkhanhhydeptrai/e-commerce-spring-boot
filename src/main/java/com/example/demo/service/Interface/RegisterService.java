package com.example.demo.service.Interface;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.RegisterResponse;
import com.example.demo.response.ApiResponse;

public interface RegisterService {
    ApiResponse<RegisterResponse> registerUser(RegisterRequest request);
}
