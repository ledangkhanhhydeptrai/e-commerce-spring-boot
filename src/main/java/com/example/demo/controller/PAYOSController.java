package com.example.demo.controller;

import com.example.demo.Enum.OrderStatus;
import com.example.demo.dto.request.PayOSCallbackRequest;
import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.dto.response.PaymentStatusResponse;
import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.PayOSService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api")
@Tag(name = "Payment")
public class PAYOSController {

    private final PayOSService payOSService;
    private final OrderRepository orderRepository; // ✅ thêm đây

    public PAYOSController(PayOSService payOSService, OrderRepository orderRepository) {
        this.payOSService = payOSService;
        this.orderRepository = orderRepository; // ✅ inject
    }


    @PostMapping("/payment/{orderId}")
    public ResponseEntity<ApiResponse<PAYOSResponse>> createPayment(@PathVariable UUID orderId) {
        return ResponseEntity.ok(payOSService.createPayment(orderId));
    }

//    @PostMapping("/payos")
//    public ApiResponse<String> payosCallback(@RequestBody PayOSCallbackRequest callbackData) {
//        return payOSService.confirmPayment(callbackData);
//    }

    @GetMapping("/payment/{orderId}")
    public ResponseEntity<ApiResponse<PaymentStatusResponse>> getPayment(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(payOSService.getPaymentByOrderId(orderId));
    }

    @PostMapping("/payment/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(payOSService.cancelPayment(orderId));
    }

    @PostMapping("/payment/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(
            @RequestParam String orderId,
            @RequestParam(required = false) String status
    ) {
        if (orderId == null || orderId.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<Void>builder()
                            .status(400)
                            .message("Missing orderId")
                            .build());
        }

        UUID uuid = UUID.fromString(orderId);
        Order order = orderRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.CANCELLED) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        // Trả về FE
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Cancel payment success")
                .build());
    }


    @PostMapping("/payment/success")
    public ResponseEntity<ApiResponse<Void>> successPayment(@RequestParam(required = false) String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .status(400)
                    .message("Missing orderId")
                    .build());
        }

        UUID uuid = UUID.fromString(orderId);
        Order order = orderRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Chỉ update trạng thái nếu chưa hoàn thành
        if (order.getStatus() != OrderStatus.COMPLETED) {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        }

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(200)
                .message("Payment success")
                .build());
    }
}
