package com.example.weather_ai.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}