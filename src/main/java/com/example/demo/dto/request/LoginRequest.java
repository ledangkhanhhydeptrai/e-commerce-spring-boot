package com.example.demo.dto.request;

import com.example.demo.Enum.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {
    private String email;
    private String password;
}
