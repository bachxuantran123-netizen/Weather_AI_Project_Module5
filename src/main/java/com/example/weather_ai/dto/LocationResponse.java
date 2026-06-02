package com.example.weather_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String cityName;
    private String alias;
    private boolean isPrimary;
}