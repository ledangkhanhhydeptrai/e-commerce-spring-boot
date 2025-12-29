package com.example.demo.service.Interface;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.response.ApiResponse;

public interface LoginService {
    LoginResponse login(LoginRequest request);
}
