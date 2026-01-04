package com.example.demo.service.Implement;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, UserRepository userRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
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


    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    @Override
    public ApiResponse<CartResponse> getMyCart() {
        User user = getCurrentUser();
        return cartRepository.findByUser(user)
                .map(cart -> new ApiResponse<>(
                        200,
                        "Get cart successfully",
                        cartMapper.mapToResponse(cart)
                ))
                .orElseGet(() -> new ApiResponse<>(
                        200,
                        "Cart is empty",
                        null
                ));
    }

    @Override
    @Transactional
    public ApiResponse<CartResponse> addToCart(AddToCartRequest request) {
        User user = getCurrentUser();

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().user(user).build()
                ));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct()
                        .getId()
                        .equals(product.getId()))
                .findFirst()
                .orElse(null);
        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .fileUrl(product.getFileUrl())
                    .build();
            cart.getCartItems().add(item); // 🔥 cascade sẽ save item
        }

        cartRepository.save(cart);

        return ApiResponse.<CartResponse>builder()
                .status(200)
                .message("Tạo cart thành công")
                .data(cartMapper.mapToResponse(cart))
                .build();
    }

    @Override
    public ApiResponse<CartResponse> updateQuantity(UUID cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException("Cart item không tồn tại", HttpStatus.NOT_FOUND));
        if (quantity == 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
        Cart cart = cartItem.getCart();
        CartResponse cartResponse = cartMapper.mapToResponse(cart);
        return ApiResponse.<CartResponse>builder()
                .status(200)
                .message("Cập nhật thành công")
                .data(cartResponse)
                .build();
    }

    @Override
    public ApiResponse<String> deleteCart(UUID id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cartRepository.delete(cart);
        return ApiResponse.<String>builder()
                .status(200)
                .message("Xóa giỏ hàng thành công")
                .data(null)
                .build();
    }
}
