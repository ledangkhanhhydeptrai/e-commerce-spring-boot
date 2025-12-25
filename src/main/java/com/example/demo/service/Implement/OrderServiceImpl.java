package com.example.demo.service.Implement;

import com.example.demo.Enum.OrderStatus;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.*;
import com.example.demo.mapper.OrderMapper;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, CartRepository cartRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public ApiResponse<List<OrderResponse>> getAllOrders() {

        List<OrderResponse> orders = orderRepository.findAll()
                .stream()
                .map(orderMapper::toOrderResponse) // Order -> OrderResponse
                .toList();

        return ApiResponse.<List<OrderResponse>>builder()
                .status(200)
                .message("Get All Order Successfully")
                .data(orders)
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Admin cannot access cart");
        }

        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public ApiResponse<OrderResponse> addOrder() {
        User user = getCurrentUser();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart has no items");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        Long totalPrice = 0L;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            Long itemTotal = product.getPrice() * quantity;
            totalPrice += itemTotal;

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(itemTotal)
                    .build();

            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .user(user)
                .totalPrice(totalPrice)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        return ApiResponse.<OrderResponse>builder()
                .status(201)
                .message("Create order successfully")
                .data(orderMapper.toOrderResponse(order))
                .build();
    }

    public ApiResponse<OrderResponse> getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không có order theo id này"));
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        return ApiResponse.<OrderResponse>builder()
                .status(200)
                .message("Get Order Successfully")
                .data(orderResponse)
                .build();
    }
}
