package com.example.weather_ai.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String cityName;
    private Double latitude; // Vĩ độ
    private Double longitude; // Kinh độ
    private String alias; // Ví dụ: "Nhà", "Công ty", "Trường học"
}