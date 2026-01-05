package com.example.demo.service.Interface;

import com.example.demo.Enum.PaymentStatus;
import com.example.demo.dto.request.PayOSCallbackRequest;
import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.dto.response.PayOSCallBack;
import com.example.demo.dto.response.PaymentStatusResponse;
import com.example.demo.response.ApiResponse;

import java.util.Map;
import java.util.UUID;

public interface PayOSService {
    ApiResponse<PAYOSResponse> createPayment(UUID orderId);
    ApiResponse<String> confirmPayment(PayOSCallbackRequest callback);
    ApiResponse<PaymentStatusResponse> getPaymentByOrderId(UUID orderId);
    ApiResponse<Void> cancelPayment(UUID orderId);
}
