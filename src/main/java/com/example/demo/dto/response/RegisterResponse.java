package com.example.demo.dto.response;

import com.example.demo.Enum.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RegisterResponse {
    private String username;
    private String password;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private UserRole role;
    private String fileUrl;
}
