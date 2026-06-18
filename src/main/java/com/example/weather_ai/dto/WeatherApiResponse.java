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
    private ForecastDto forecast;
    private AlertsDto alerts;

    @Getter
    @Setter
    public static class LocationDto {
        private String name;
        private String country;
        private Double lat;
        private Double lon;
    }

    @Getter
    @Setter
    public static class CurrentDto {
        @JsonProperty("temp_c")
        private Double tempC;
        private Double uv;
        private Double humidity;
        @JsonProperty("wind_kph")
        private Double windKph;
        @JsonProperty("vis_km")
        private Double visKm;
        private ConditionDto condition;
    }

    @Getter
    @Setter
    public static class ConditionDto {
        private String text;
        private String icon;
    }

    @Getter
    @Setter
    public static class ForecastDto {
        private List<ForecastdayDto> forecastday;
    }

    @Getter
    @Setter
    public static class ForecastdayDto {
        private String date;
        private DayDto day;
        private List<HourDto> hour;
    }

    @Getter
    @Setter
    public static class DayDto {
        @JsonProperty("maxtemp_c")
        private Double maxtempC;
        @JsonProperty("mintemp_c")
        private Double mintempC;
    }

    @Getter
    @Setter
    public static class HourDto {
        private String time;
        @JsonProperty("temp_c")
        private Double tempC;
        private ConditionDto condition;
    }

    @Getter
    @Setter
    public static class AlertsDto {
        private List<AlertItemDto> alert;
    }

    @Getter
    @Setter
    public static class AlertItemDto {
        private String headline;
        private String severity;
        private String desc;
    }
}