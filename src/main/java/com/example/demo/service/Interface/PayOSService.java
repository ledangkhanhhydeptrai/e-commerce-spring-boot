package com.example.demo.service.Interface;

import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.response.ApiResponse;

import java.util.UUID;

public interface PayOSService {
    ApiResponse<PAYOSResponse> createPayment(UUID orderId);
}
