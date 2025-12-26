package com.example.demo.service.Implement;

import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.entity.Order;
import com.example.demo.payos.PayOSSignatureUtil;
import com.example.demo.repository.OrderRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.PayOSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PayOSServiceImpl implements PayOSService {

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Value("${payos.base-url}")
    private String baseUrl;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final OrderRepository orderRepository;

    public PayOSServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public ApiResponse<PAYOSResponse> createPayment(UUID orderId) {

        // 1️⃣ Lấy order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2️⃣ Amount (PayOS >= 1000)
        int amount = order.getTotalPrice().intValue();
        if (amount < 1000) {
            throw new RuntimeException("Amount must be >= 1000 VND");
        }

        // 3️⃣ orderCode KHÔNG TRÙNG (QUAN TRỌNG)
        long orderCode = System.currentTimeMillis();
        order.setPayosOrderCode(orderCode);
        orderRepository.save(order);

        // 4️⃣ Description <= 25 ký tự
        String description = "ORDER_" + orderCode;
        if (description.length() > 25) {
            description = description.substring(0, 25);
        }

        // 5️⃣ Ký signature
        String signature = PayOSSignatureUtil.sign(
                amount,
                cancelUrl,
                description,
                orderCode,
                returnUrl,
                checksumKey
        );

        // 6️⃣ Body gửi PayOS
        Map<String, Object> body = new HashMap<>();
        body.put("orderCode", orderCode);
        body.put("amount", amount);
        body.put("description", description);
        body.put("returnUrl", returnUrl);
        body.put("cancelUrl", cancelUrl);
        body.put("signature", signature);

        // 7️⃣ Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", clientId);
        headers.set("x-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // 🔥 DEBUG REQUEST
        System.out.println("PAYOS REQUEST BODY = " + body);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                baseUrl + "/v2/payment-requests",
                entity,
                Map.class
        );

        // 🔥 DEBUG RESPONSE
        System.out.println("PAYOS STATUS = " + response.getStatusCode());
        System.out.println("PAYOS RAW RESPONSE = " + response.getBody());

        Map<String, Object> resBody = response.getBody();

        if (resBody == null) {
            throw new RuntimeException("PayOS response body is null");
        }

        if (!resBody.containsKey("data")) {
            throw new RuntimeException("PayOS error: " + resBody);
        }

        Map<String, Object> data = (Map<String, Object>) resBody.get("data");

        PAYOSResponse payosResponse = PAYOSResponse.builder()
                .checkoutUrl(data.get("checkoutUrl").toString())
                .paymentLinkId(data.get("paymentLinkId").toString())
                .orderCode(orderCode)
                .build();

        return ApiResponse.<PAYOSResponse>builder()
                .status(200)
                .message("Tạo link thanh toán PayOS thành công")
                .data(payosResponse)
                .build();
    }
}
