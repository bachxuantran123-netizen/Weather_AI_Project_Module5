package com.example.weather_ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class WeatherApiResponse {
    private LocationDto location;
    private CurrentDto current;

    // BỔ SUNG: Khối dữ liệu dự báo
    private ForecastDto forecast;

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
        private Double tempC;
        private Double uv;
        private Double humidity; // Thêm Độ ẩm
        @JsonProperty("wind_kph")
        private Double windKph; // Thêm Sức gió
        @JsonProperty("vis_km")
        private Double visKm; // Thêm Tầm nhìn
        private ConditionDto condition;
    }

    @Getter
    @Setter
    public static class ConditionDto {
        private String text;
        private String icon; // Lấy thêm link ảnh icon thời tiết
    }

    // --- CÁC CLASS MỚI CHO DỰ BÁO ---
    @Getter
    @Setter
    public static class ForecastDto {
        private List<ForecastdayDto> forecastday;
    }

    @Getter
    @Setter
    public static class ForecastdayDto {
        private String date; // Ngày dự báo
        private DayDto day;  // Tổng quan cả ngày
        private List<HourDto> hour; // Chi tiết từng giờ trong ngày
    }

    @Getter
    @Setter
    public static class DayDto {
        @JsonProperty("maxtemp_c")
        private Double maxtempC; // Nhiệt độ cao nhất
        @JsonProperty("mintemp_c")
        private Double mintempC; // Nhiệt độ thấp nhất
    }

    @Getter
    @Setter
    public static class HourDto {
        private String time; // Thời gian (VD: "2023-10-25 10:00")
        @JsonProperty("temp_c")
        private Double tempC;
        private ConditionDto condition;
    }
}