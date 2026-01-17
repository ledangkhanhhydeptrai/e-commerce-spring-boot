package com.example.demo.controller;

import com.example.demo.dto.request.CreateProfileRequest;
import com.example.demo.dto.response.ProfileResponse;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.Interface.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getUserProfile() {
        return ResponseEntity.ok(profileService.getUserProfile());
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(@RequestBody CreateProfileRequest request) {
        return ResponseEntity.ok(profileService.updateUserProfile(request));
    }
}
