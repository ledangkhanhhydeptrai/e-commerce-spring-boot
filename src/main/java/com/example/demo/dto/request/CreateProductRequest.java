package com.example.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateProductRequest {

    @Schema(description = "Tên sản phẩm")
    private String name;

    @Schema(description = "Giá sản phẩm")
    private Long price;

    @Schema(description = "Số lượng sản phẩm")
    private int quantity;

    @Schema(type = "string", format = "binary")
    private MultipartFile file; // ✅ Swagger hiểu được
}