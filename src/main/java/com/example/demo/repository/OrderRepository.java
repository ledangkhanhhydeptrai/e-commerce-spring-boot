package com.example.demo.repository;

import com.example.demo.Enum.OrderStatus;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByPayosOrderCode(long orderCode);
    Optional<Order> findByUserAndStatus(User user, OrderStatus status);

}
