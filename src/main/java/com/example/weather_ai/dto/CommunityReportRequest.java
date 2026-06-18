package com.example.weather_ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityReportRequest {
    private String cityName;
    private String reportType;
    private String description;
}