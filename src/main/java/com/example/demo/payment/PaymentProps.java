package com.example.demo.payment;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProps {
    // --------- Getter & Setter ----------
    private String checkoutUrl;
    private String paymentLinkId;
    private String orderCode;
    private String paymentStatus;
    private String orderStatus;
}
