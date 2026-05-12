package com.example.weather_ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherApiResponse {
    private LocationDto location;
    private CurrentDto current;

    @Getter
    @Setter
    public static class LocationDto {
        private String name;
        private String country;
    }

    @Getter
    @Setter
    public static class CurrentDto {
        @JsonProperty("temp_c")
        private Double tempC; // Nhiệt độ C

        private Double uv; // Chỉ số tia cực tím

        private ConditionDto condition; // Trạng thái (mưa, nắng)
    }

    @Getter
    @Setter
    public static class ConditionDto {
        private String text; // Ví dụ: "Heavy rain"
    }
}
