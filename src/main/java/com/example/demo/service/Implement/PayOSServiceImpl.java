package com.example.demo.service.Implement;

import com.example.demo.Enum.OrderStatus;
import com.example.demo.dto.request.PayOSCallbackRequest;
import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.dto.response.PaymentStatusResponse;
import com.example.demo.entity.Order;
import com.example.demo.payment.PaymentProps;
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
import java.util.Optional;
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
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ApiResponse.<PAYOSResponse>builder()
                    .status(404)
                    .message("Order not found")
                    .data(null)
                    .build();
        }

        Order order = optionalOrder.get();

        // 2️⃣ Thêm orderId vào return/cancel URL
        String cancelUrlWithOrderId =
                cancelUrl + "/" + order.getId() + "?status=CANCELLED";

        String returnUrlWithOrderId =
                returnUrl + "/" + order.getId() + "?status=PAID";

        // 3️⃣ Amount >= 1000
        int amount = order.getTotalPrice().intValue();
        if (amount < 1000) {
            return ApiResponse.<PAYOSResponse>builder()
                    .status(400)
                    .message("Amount must be >= 1000 VND")
                    .data(null)
                    .build();
        }

        try {
            // 4️⃣ Tạo orderCode
            long orderCode = System.currentTimeMillis();
            order.setPayosOrderCode(orderCode);
            orderRepository.save(order);

            // 5️⃣ Description
            String description = "ORDER_" + orderCode;
            if (description.length() > 25) description = description.substring(0, 25);

            // 6️⃣ Ký signature với URL đã thêm orderId
            String signature = PayOSSignatureUtil.sign(
                    amount, cancelUrlWithOrderId, description, orderCode, returnUrlWithOrderId, checksumKey
            );

            // 7️⃣ Body gửi PayOS
            Map<String, Object> body = new HashMap<>();
            body.put("orderCode", orderCode);
            body.put("amount", amount);
            body.put("description", description);
            body.put("returnUrl", returnUrlWithOrderId);
            body.put("cancelUrl", cancelUrlWithOrderId);
            body.put("signature", signature);

            // 8️⃣ Header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-client-id", clientId);
            headers.set("x-api-key", apiKey);

            System.out.println("Calling PayOS URL: " + baseUrl + "/v2/payment-requests");
            System.out.println("Headers: " + headers);
            System.out.println("Body: " + body);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/v2/payment-requests",
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            // 9️⃣ Check response an toàn
            Map<String, Object> resBody = response.getBody();
            if (resBody == null) {
                return ApiResponse.<PAYOSResponse>builder()
                        .status(502)
                        .message("PayOS service error: response body null")
                        .data(null)
                        .build();
            }

            Object dataObj = resBody.get("data");
            if (dataObj == null) {
                return ApiResponse.<PAYOSResponse>builder()
                        .status(502)
                        .message("PayOS service error: data field is null, raw response: " + resBody)
                        .data(null)
                        .build();
            }

            Map<String, Object> data = (Map<String, Object>) dataObj;
            String checkoutUrl = data.get("checkoutUrl").toString();
            String paymentLinkId = data.get("paymentLinkId").toString();

            // 10️⃣ Lưu vào order
            order.setCheckoutUrl(checkoutUrl);
            order.setPaymentLinkId(paymentLinkId);
            orderRepository.save(order);

            // 11️⃣ Trả response cho FE
            PAYOSResponse payosResponse = PAYOSResponse.builder()
                    .checkoutUrl(checkoutUrl)
                    .paymentLinkId(paymentLinkId)
                    .orderCode(orderCode)
                    .build();

            return ApiResponse.<PAYOSResponse>builder()
                    .status(200)
                    .message("Tạo link thanh toán PayOS thành công")
                    .data(payosResponse)
                    .build();

        } catch (Exception e) {
            return ApiResponse.<PAYOSResponse>builder()
                    .status(502)
                    .message("PayOS service error: " + e.getMessage())
                    .data(null)
                    .build();
        }
    }


    @Override
    public ApiResponse<String> confirmPayment(PayOSCallbackRequest callback) {
        // 1. Lấy dữ liệu từ callback
        long orderCode = callback.getOrderCode();
        String status = callback.getStatus();
        String signature = callback.getSignature();

        // 2. Lấy order từ DB
        Order order = orderRepository.findByPayosOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 3. Tạo rawData để verify signature theo PayOS (xếp key alphabet, bỏ signature)
        String rawData = "amount=" + callback.getAmount() +
                "&description=" + callback.getDescription() +
                "&orderCode=" + callback.getOrderCode() +
                "&status=" + callback.getStatus();

        System.out.println("---- PAYOS CALLBACK DEBUG ----");
        System.out.println("RAW Data for signature: " + rawData);
        System.out.println("Received signature: " + signature);

        // 4. Verify signature
        boolean isValid = PayOSSignatureUtil.verifyCallback(rawData, checksumKey, signature);
        if (!isValid) {
            throw new RuntimeException("Invalid signature");
        }

        // 5. Cập nhật trạng thái order theo nghiệp vụ
        if ("PAID".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.COMPLETED); // chỉ khi PAID mới COMPLETED
        } else if ("CANCELLED".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.CANCELLED);
        } else {
            order.setStatus(OrderStatus.PENDING); // PENDING hoặc trạng thái khác
        }

        orderRepository.save(order);

        // 6. Trả response
        return ApiResponse.<String>builder()
                .status(200)
                .message("Payment confirmed")
                .data(null)
                .build();
    }

    @Override
    public ApiResponse<PaymentStatusResponse> getPaymentByOrderId(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentStatusResponse response = PaymentStatusResponse.builder()
                .orderCode(order.getPayosOrderCode())
                .checkoutUrl(order.getCheckoutUrl()) // ⚠️ xem lưu ở đâu
                .paymentLinkId(order.getPaymentLinkId())
                .orderStatus(order.getStatus())
                .build();

        return ApiResponse.<PaymentStatusResponse>builder()
                .status(200)
                .message("Get payment status success")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<PaymentProps> cancelPayment(String orderId, String status) {

        if (orderId == null || orderId.isBlank()) {
            return ApiResponse.<PaymentProps>builder()
                    .status(400)
                    .message("❌ orderId is required")
                    .data(null)
                    .build();
        }

        // 1️⃣ Tìm order theo UUID hoặc payosOrderCode
        Order order = null;

        try {
            UUID uuid = UUID.fromString(orderId);
            order = orderRepository.findById(uuid).orElse(null);
        } catch (IllegalArgumentException ignored) {
        }

        if (order == null) {
            try {
                Long code = Long.parseLong(orderId);
                order = orderRepository.findByPayosOrderCode(code).orElse(null);
            } catch (NumberFormatException ignored) {
            }
        }

        if (order == null) {
            return ApiResponse.<PaymentProps>builder()
                    .status(404)
                    .message("❌ Order not found")
                    .data(null)
                    .build();
        }

        // 2️⃣ Update status theo nghiệp vụ
        if ("CANCELLED".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.CANCELLED);
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            order.setStatus(OrderStatus.PENDING);
        }

        orderRepository.save(order);

        // 3️⃣ Build PaymentProps
        PaymentProps result = new PaymentProps();
        result.setOrderCode(order.getPayosOrderCode().toString());
        result.setPaymentStatus(status);
        result.setCheckoutUrl(order.getCheckoutUrl());
        result.setPaymentLinkId(order.getPaymentLinkId());
        result.setOrderStatus(order.getStatus().name());

        return ApiResponse.<PaymentProps>builder()
                .status(200)
                .message("✅ Cancel success")
                .data(result)
                .build();
    }

    @Override
    public ApiResponse<Void> markPaymentSuccess(String orderId) {

        if (orderId == null || orderId.isBlank()) {
            return ApiResponse.<Void>builder()
                    .status(400)
                    .message("Missing orderId")
                    .build();
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            return ApiResponse.<Void>builder()
                    .status(400)
                    .message("Invalid orderId")
                    .build();
        }

        Order order = orderRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ❗ Nghiệp vụ chặt
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return ApiResponse.<Void>builder()
                    .status(400)
                    .message("Order already cancelled")
                    .build();
        }

        if (order.getStatus() == OrderStatus.COMPLETED) {
            return ApiResponse.<Void>builder()
                    .status(200)
                    .message("Order already completed")
                    .build();
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        return ApiResponse.<Void>builder()
                .status(200)
                .message("Payment success")
                .build();
    }

}
