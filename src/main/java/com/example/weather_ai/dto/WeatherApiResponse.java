package com.example.weather_ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherApiResponse {
    private LocationDto location;
    private CurrentDto current;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDto {
        private String name;
        private String country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentDto {
        @JsonProperty("temp_c")
        private Double tempC; // Nhiệt độ C

        private Double uv; // Chỉ số tia cực tím

        private ConditionDto condition; // Trạng thái (mưa, nắng)
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionDto {
        private String text; // Ví dụ: "Heavy rain"
    }
}
