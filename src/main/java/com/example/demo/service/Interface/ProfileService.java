package com.example.demo.service.Interface;

import com.example.demo.dto.request.CreateProfileRequest;
import com.example.demo.dto.response.ProfileResponse;
import com.example.demo.response.ApiResponse;

public interface ProfileService {
    ApiResponse<ProfileResponse> getUserProfile();
    ApiResponse<ProfileResponse> updateUserProfile(CreateProfileRequest request);
}
