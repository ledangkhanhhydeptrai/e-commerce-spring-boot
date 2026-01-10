package com.example.demo.dto.response;


import com.example.demo.Enum.OrderStatus;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {
    private Long orderCode;
    private String checkoutUrl;
    private String paymentLinkId;
    private OrderStatus orderStatus;
    private String paymentStatus;
}
