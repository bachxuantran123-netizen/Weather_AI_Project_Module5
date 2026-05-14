package com.example.weather_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class WeatherAdviceResponse {
    private String cityName;
    private Double temperature;
    private String condition;
    private String aiAdvice;
}