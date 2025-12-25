package com.example.demo.service.Implement;

import com.example.demo.dto.response.PAYOSResponse;
import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.PayOSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import java.util.UUID;

@Service
public class PayOSServiceImpl implements PayOSService {

    private final PayOS payOS;
    private final OrderRepository orderRepository;

    public PayOSServiceImpl(PayOS payOS, OrderRepository orderRepository) {
        this.payOS = payOS;
        this.orderRepository = orderRepository;
    }

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Override
    public ApiResponse<PAYOSResponse> createPayment(UUID orderId) {
        // 1️⃣ Lấy order từ DB
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2️⃣ Chuẩn hóa amount: PayOS yêu cầu int (VNĐ / 1000 nếu cần)
        int amount = (int) (order.getTotalPrice() / 1000);

        // 3️⃣ Tạo orderCode kiểu long riêng cho PayOS (< 1 tỷ)
        long orderCode = System.currentTimeMillis() % 1_000_000_000L;
        order.setPayosOrderCode(orderCode); // lưu vào DB để callback
        orderRepository.save(order);

        // 4️⃣ Chuẩn hóa description tối đa 25 ký tự
        String desc = "Order#" + order.getId();
        if (desc.length() > 25) {
            desc = desc.substring(0, 25);
        }

        // 5️⃣ In debug để kiểm tra
        System.out.println("===== DEBUG PAYOS =====");
        System.out.println("Client ID: " + clientId);
        System.out.println("OrderCode: " + orderCode);
        System.out.println("Amount: " + amount);
        System.out.println("Description: " + desc);
        System.out.println("Return URL: " + returnUrl);
        System.out.println("Cancel URL: " + cancelUrl);
        System.out.println("Checksum Key: " + checksumKey);
        System.out.println("=========================");

        try {
            // 6️⃣ Tạo PaymentData
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(amount)
                    .description(desc)
                    .returnUrl(returnUrl)
                    .cancelUrl(cancelUrl)
                    .build();

            // 7️⃣ Tạo link thanh toán
            CheckoutResponseData response = payOS.createPaymentLink(paymentData);

            // 8️⃣ Trả về response
            PAYOSResponse payosResponse = new PAYOSResponse(
                    response.getCheckoutUrl(),
                    response.getPaymentLinkId(),
                    orderCode
            );

            return ApiResponse.<PAYOSResponse>builder()
                    .status(200)
                    .message("Tạo link thanh toán thành công")
                    .data(payosResponse)
                    .build();

        } catch (Exception e) {
            System.err.println("PayOS createPayment failed: " + e.getMessage());
            throw new RuntimeException("Thanh toán PayOS thất bại", e);
        }
    }
}
