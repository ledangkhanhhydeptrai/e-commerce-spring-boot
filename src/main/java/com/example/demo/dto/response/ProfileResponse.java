package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProfileResponse {
    private UUID id;
    private String image;
    private String username;
    private String email;
}
