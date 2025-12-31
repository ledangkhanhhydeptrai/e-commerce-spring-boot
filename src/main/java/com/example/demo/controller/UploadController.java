package com.example.demo.controller;

import com.example.demo.dto.response.UploadResponse;
import com.example.demo.service.Interface.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @Operation(
            summary = "Upload image",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Upload success",
                            content = @Content(schema = @Schema(implementation = UploadResponse.class))),
                    @ApiResponse(responseCode = "400", description = "File empty"),
                    @ApiResponse(responseCode = "500", description = "Upload failed")
            }
    )
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty or missing");
        }

        String url = cloudinaryService.uploadFile(file);
        return ResponseEntity.ok(new UploadResponse(url, file.getOriginalFilename()));
    }
}
