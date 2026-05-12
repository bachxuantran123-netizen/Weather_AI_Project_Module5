package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.WeatherAdviceResponse;
import com.example.weather_ai.service.WeatherFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherApiController {

    private final WeatherFacadeService weatherFacadeService;
    // Mở Link Này Để Kiểm Tra API Nhé AE !!! http://localhost:8080/api/v1/weather/current?city=Hanoi
    @GetMapping("/current")
    public Mono<ResponseEntity<WeatherAdviceResponse>> getCurrentWeather(@RequestParam String city) {
        return weatherFacadeService.getWeatherWithAdvice(city)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
