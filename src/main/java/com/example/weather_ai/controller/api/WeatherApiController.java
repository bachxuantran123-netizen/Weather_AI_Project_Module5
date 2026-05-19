package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.WeatherAdviceResponse;
import com.example.weather_ai.service.WeatherFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherApiController {

    private final WeatherFacadeService weatherFacadeService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeatherWithAdvice(@RequestParam String city) {
        WeatherAdviceResponse response = weatherFacadeService.getWeatherWithAdvice(city);
        return ResponseEntity.ok(response);
    }
}