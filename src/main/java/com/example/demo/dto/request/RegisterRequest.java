package com.example.demo.dto.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class RegisterRequest {
    private String email;
    private String username;
    private String password;
    private MultipartFile fileUrl;
}
