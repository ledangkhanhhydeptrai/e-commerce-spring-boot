package com.example.demo.controller;

import com.example.demo.dto.request.PayOSCallbackRequest;
import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.PayOSService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment")
public class PAYOSController {

    private final PayOSService payOSService;

    public PAYOSController(PayOSService payOSService) {
        this.payOSService = payOSService;
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PAYOSResponse>> createPayment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(payOSService.createPayment(orderId));
    }

    @PostMapping("/payos")
    public ApiResponse<String> payosCallback(@RequestBody PayOSCallbackRequest callbackData) {
        return payOSService.confirmPayment(callbackData);
    }


}
