package com.example.demo.service.Implement;

import com.example.demo.dto.response.UserResponse;
import com.example.demo.mapper.UserResponseMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserResponseMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserResponseMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse<List<UserResponse>> getAllUser() {
        List<UserResponse> responses = userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();

        return ApiResponse.<List<UserResponse>>builder().
                status(200)
                .message("Get all users successfully")
                .data(responses)
                .build();
    }
}
