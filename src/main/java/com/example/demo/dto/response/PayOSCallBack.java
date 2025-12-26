package com.example.demo.dto.response;

import com.example.demo.Enum.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class PayOSCallBack {
    private long orderCode;
    private String status;
    private int amount;
    private String description;
    private String signature;
    // getter setter
}
