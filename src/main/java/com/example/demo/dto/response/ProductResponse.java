package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@Builder
public class ProductResponse {
    private UUID id;
    private String name;
    private Long price;
    private int quantity;
    private String image; // Thay vì MultipartFile
}
