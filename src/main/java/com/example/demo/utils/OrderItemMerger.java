package com.example.demo.utils;

import com.example.demo.entity.OrderItem;
import java.util.*;

public class OrderItemMerger {

    /**
     * Gom các OrderItem trùng productId thành 1 item
     * quantity và price cộng dồn
     */
    public static List<OrderItem> mergeItems(List<OrderItem> items) {
        Map<UUID, OrderItem> merged = new LinkedHashMap<>(); // giữ thứ tự

        for (OrderItem item : items) {
            UUID productId = item.getProduct().getId();

            if (merged.containsKey(productId)) {
                OrderItem existing = merged.get(productId);
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                existing.setPrice(existing.getPrice() + item.getPrice());
            } else {
                // tạo copy mới để không ảnh hưởng item gốc
                OrderItem copy = OrderItem.builder()
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .fileUrl(item.getFileUrl())
                        .build();
                merged.put(productId, copy);
            }
        }

        return new ArrayList<>(merged.values());
    }
}
