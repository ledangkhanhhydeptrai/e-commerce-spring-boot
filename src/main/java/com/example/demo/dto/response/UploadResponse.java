package com.example.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    @Schema(description = "URL của file đã upload")
    private String url;

    @Schema(description = "Tên file gốc")
    private String fileName;

    // constructor, getter, setter
}
