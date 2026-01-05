package com.example.demo.dto.response;


import com.example.demo.Enum.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentStatusResponse {
    private Long orderCode;
    private String checkoutUrl;
    private String paymentLinkId;
    private OrderStatus orderStatus;
}
