package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.WeatherAdviceResponse;
import com.example.weather_ai.service.SearchHistoryService;
import com.example.weather_ai.service.WeatherFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherApiController {

    private final WeatherFacadeService weatherFacadeService;
    private final SearchHistoryService searchHistoryService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeatherWithAdvice(@RequestParam String city, Principal principal) {

        // 1. Kích hoạt lưu lịch sử chạy ngầm (Không block luồng chính)
        if (principal != null) {
            String username = principal.getName();
            searchHistoryService.logSearchAsync(username, city);
        }

        // 2. Gọi logic lấy thời tiết (vẫn tận dụng được Redis Cache nguyên vẹn)
        WeatherAdviceResponse response = weatherFacadeService.getWeatherWithAdvice(city);
        return ResponseEntity.ok(response);
    }
}