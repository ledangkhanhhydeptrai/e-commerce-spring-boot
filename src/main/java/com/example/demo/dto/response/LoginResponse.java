package com.example.demo.dto.response;

import com.example.demo.Enum.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private UserRole role;
}
