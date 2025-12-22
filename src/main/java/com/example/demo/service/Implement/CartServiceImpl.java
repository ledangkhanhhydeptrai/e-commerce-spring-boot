package com.example.demo.service.Implement;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.response.CartResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.mapper.CartMapper;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
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
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> getOrCreateCart(user));
        return new ApiResponse<>(
                200,
                "Get cart successfully",
                cartMapper.mapToResponse(cart)
        );
    }

    @Override
    public CartResponse addToCart(AddToCartRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        CartItem item = cart.getCartItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);
        if (item == null) {
            item = CartItem.builder()
                    .cart(cart)
                    .price(product.getPrice())
                    .product(product)
                    .quantity(request.quantity())
                    .build();
            cart.getCartItems().add(item);
        } else {
            item.setQuantity(item.getQuantity() + request.quantity());
        }
        cartRepository.save(cart);
        return cartMapper.mapToResponse(cart);
    }
}
