package com.example.weather_ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeatherAdviceResponse {
    private String cityName;
    private Double temperature;
    private String condition;
    private Double tempHigh;
    private Double tempLow;
    private Double uvIndex;
    private Double humidity;
    private Double windSpeed;
    private Double visibility;
    private List<HourlyForecastDto> hourlyForecast;
    private AiAdviceDto aiAdvice;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HourlyForecastDto {
        private String time;
        private Double temp;
        private String iconUrl;
    }
}