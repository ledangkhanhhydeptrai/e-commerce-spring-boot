package com.example.demo.entity;

import com.example.demo.Enum.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,     // 🔥 QUAN TRỌNG NHẤT
            orphanRemoval = true
    )
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    private Long totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    @Column(unique = true)
    private Long payosOrderCode;
}
