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

    // THÊM: Các thông số chi tiết cho UI Mobile
    private Double tempHigh;
    private Double tempLow;
    private Double uvIndex;
    private Double humidity;
    private Double windSpeed;
    private Double visibility;

    // THÊM: Danh sách dự báo hàng giờ
    private List<HourlyForecastDto> hourlyForecast;

    private AiAdviceDto aiAdvice;

    // CLASS CON: Hứng dữ liệu từng giờ
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HourlyForecastDto {
        private String time; // Chỉ lấy "10:00", "11:00"
        private Double temp;
        private String iconUrl;
    }
}