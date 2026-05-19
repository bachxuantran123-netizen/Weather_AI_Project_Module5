package com.example.weather_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherAdviceResponse {
    private String cityName;
    private Double temperature;
    private String condition;
    private String aiAdvice;
}