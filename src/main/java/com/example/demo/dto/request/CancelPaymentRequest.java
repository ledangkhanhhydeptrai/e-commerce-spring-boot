package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class CancelPaymentRequest {
    private String orderId;
    private String status;
}
