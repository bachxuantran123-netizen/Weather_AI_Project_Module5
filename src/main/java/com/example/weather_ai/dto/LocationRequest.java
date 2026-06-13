package com.example.weather_ai.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String cityName;
    private Double latitude;
    private Double longitude;
    private String alias;
}