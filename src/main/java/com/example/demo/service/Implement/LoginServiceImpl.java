package com.example.demo.service.Implement;

import com.example.demo.Enum.UserRole;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.Interface.LoginService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public LoginServiceImpl(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ApiResponse<LoginResponse> loginRequest(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        } catch (BadCredentialsException e) {
            return ApiResponse.<LoginResponse>builder()
                    .status(400)
                    .message("Sai password hoặc username")
                    .data(null)
                    .build();
        }
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("Username not found"));
        String token = jwtUtil.generateToken(user.getUsername());
        UserRole role = user.getRole().getName();
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .role(role)
                .build();
        return ApiResponse.<LoginResponse>builder()
                .status(200)
                .message("Đăng nhập thành công")
                .data(response)
                .build();
    }
}
