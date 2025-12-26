package com.example.demo.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    @Value("${payos.client-id}")
    private String clientId;

    @Value("${payos.api-key}")
    private String apiKey;

    @Value("${payos.checksum-key}")
    private String checksumKey;

    @Bean
    public PayOS payOS() {
        System.out.println("PayOS init:");
        System.out.println("clientId = " + clientId);
        System.out.println("apiKey = " + apiKey);
        System.out.println("checksumKey = " + checksumKey);

        return new PayOS(clientId, apiKey, checksumKey);
    }
}
