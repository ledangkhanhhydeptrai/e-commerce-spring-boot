package com.example.demo.repository;

import com.example.demo.entity.CartItem;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CartItemRepository extends CrudRepository<CartItem, UUID> {
}
