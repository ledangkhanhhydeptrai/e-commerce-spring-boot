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
import com.example.demo.utils.OrderItemMerger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        User user = getCurrentUser();

        Optional<Order> orders = orderRepository.findByUserAndStatus(user, OrderStatus.PENDING);
        List<OrderResponse> orderResponses = orders.stream().map(order -> {
            // Gom các item trùng nhau
            List<OrderItem> mergedItems = OrderItemMerger.mergeItems(order.getOrderItems());
            order.setOrderItems(mergedItems);
            return orderMapper.toOrderResponse(order);
        }).collect(Collectors.toList());

        return ApiResponse.<List<OrderResponse>>builder().status(200).message("Get All Order Successfully").data(orderResponses).build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Admin cannot access cart");
        }

        return userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public ApiResponse<OrderResponse> addOrder() {
        User user = getCurrentUser();

        // Lấy giỏ hàng của user
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart has no items");
        }

        // Lấy đơn PENDING hiện tại hoặc tạo mới
        Order order = orderRepository.findByUserAndStatus(user, OrderStatus.PENDING)
                .orElseGet(() -> {
                    Order newOrder = Order.builder()
                            .user(user)
                            .status(OrderStatus.PENDING)
                            .createdAt(LocalDate.now())
                            .updatedAt(LocalDate.now())
                            .totalPrice(0L)
                            .build();
                    newOrder.setOrderItems(new ArrayList<>());
                    return newOrder;
                });

        // Gom các sản phẩm trùng nhau
        Map<UUID, OrderItem> productMap = order.getOrderItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getId(),
                        item -> item,
                        (existing, newItem) -> existing // nếu trùng thì giữ cái cũ
                ));

        for (CartItem cartItem : cart.getCartItems()) {
            UUID productId = cartItem.getProduct().getId();
            long itemTotal = cartItem.getProduct().getPrice() * cartItem.getQuantity();

            if (productMap.containsKey(productId)) {
                // Nếu đã có trong order, cộng quantity và price
                OrderItem existingItem = productMap.get(productId);
                existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
                existingItem.setPrice(existingItem.getPrice() + itemTotal);
            } else {
                // Nếu chưa có, tạo mới
                OrderItem newItem = OrderItem.builder()
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .fileUrl(cartItem.getProduct().getFileUrl())
                        .price(itemTotal)
                        .order(order)
                        .build();
                order.getOrderItems().add(newItem);
                productMap.put(productId, newItem);
            }
        }

        // Cập nhật tổng tiền và thời gian
        long totalPrice = order.getOrderItems().stream().mapToLong(OrderItem::getPrice).sum();
        order.setTotalPrice(totalPrice);
        order.setUpdatedAt(LocalDate.now());

        // Lưu order
        orderRepository.save(order);

        return ApiResponse.<OrderResponse>builder()
                .status(201)
                .message("Create order successfully")
                .data(orderMapper.toOrderResponse(order))
                .build();
    }


    public ApiResponse<OrderResponse> getOrderById(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Không có order theo id này"));
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        return ApiResponse.<OrderResponse>builder().status(200).message("Get Order Successfully").data(orderResponse).build();
    }
}
