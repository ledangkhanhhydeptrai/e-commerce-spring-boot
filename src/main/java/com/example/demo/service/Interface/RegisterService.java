package com.example.demo.service.Interface;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.RegisterResponse;
import com.example.demo.response.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface RegisterService {
    void registerUser(RegisterRequest request, MultipartFile file);
}
