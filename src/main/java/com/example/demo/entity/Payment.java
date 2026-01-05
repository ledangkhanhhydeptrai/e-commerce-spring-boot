package com.example.demo.entity;

import com.example.demo.Enum.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String orderCode;

    @Column(nullable = false, unique = true)
    private String paymentLinkId;

    @Column(nullable = false, length = 500)
    private String checkoutUrl;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long amount;

    private LocalDate expiredAt;

    private LocalDate createdAt;

    private LocalDate paidAt;
}
