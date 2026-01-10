package com.example.demo.controller;

import com.example.demo.dto.response.PaymentStatusResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.PayOSService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Payment")
public class PAYOSController {

    private final PayOSService payOSService;

    public PAYOSController(PayOSService payOSService) {
        this.payOSService = payOSService;
    }

    // -------------------------------
    // Create Payment
    // POST /api/payment/{orderId}
    // -------------------------------
    @PostMapping("/payment/{orderId}")
    public ResponseEntity<?> createPayment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(payOSService.createPayment(orderId));
    }

    // -------------------------------
    // Get Payment by OrderId
    // GET /api/payment/{orderId}
    // -------------------------------
    @GetMapping("/payment/{orderId}")
    public ResponseEntity<?> getPayment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(payOSService.getPaymentByOrderId(orderId));
    }


    // -------------------------------
    // Cancel Payment (GET version)
    // GET /api/payment/cancel-order?orderId=...&status=...
    // -------------------------------
    @PostMapping("/payment/cancel-order")
    public ResponseEntity<ApiResponse<PaymentStatusResponse>> cancelPaymentGet(
            @RequestParam String orderId,
            @RequestParam(defaultValue = "CANCELLED") String status
    ) {
        return ResponseEntity.ok(payOSService.cancelPayment(orderId, status));
    }

    // -------------------------------
    // Success Payment
    // POST /api/payment/success?orderId=...
    // -------------------------------
    @PostMapping("/payment/success")
    public ResponseEntity<ApiResponse<Void>> successPayment(
            @RequestParam String orderId
    ) {
        return ResponseEntity.ok(payOSService.markPaymentSuccess(orderId));
    }
}
