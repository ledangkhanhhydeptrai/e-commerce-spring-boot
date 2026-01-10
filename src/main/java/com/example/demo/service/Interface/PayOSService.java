package com.example.demo.service.Interface;

import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.dto.response.PaymentStatusResponse;
import com.example.demo.response.ApiResponse;

import java.util.Map;
import java.util.UUID;

public interface PayOSService {
    ApiResponse<PAYOSResponse> createPayment(UUID orderId);
    ApiResponse<PaymentStatusResponse> getPaymentByOrderId(UUID orderId);
    ApiResponse<PaymentStatusResponse> cancelPayment(String orderId, String status);
    ApiResponse<Void> markPaymentSuccess(String orderId);
}
