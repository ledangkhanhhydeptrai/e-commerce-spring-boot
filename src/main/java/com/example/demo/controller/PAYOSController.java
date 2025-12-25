package com.example.demo.controller;

import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.PayOSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PAYOSController {

    private final PayOSService payOSService;

    public PAYOSController(PayOSService payOSService) {
        this.payOSService = payOSService;
    }
    @PostMapping("{id}")
    public ResponseEntity<ApiResponse<PAYOSResponse>> payOS(@PathVariable UUID id){
        return ResponseEntity.ok(payOSService.createPayment(id));
    }
}
