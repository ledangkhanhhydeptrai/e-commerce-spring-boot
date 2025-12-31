package com.example.demo.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", "${cloudinary.cloud_name}",
                        "api_key", "${cloudinary.api_key}",
                        "api_secret", "${cloudinary.api_secret}",
                        "secure", true
                )
        );
    }
}
