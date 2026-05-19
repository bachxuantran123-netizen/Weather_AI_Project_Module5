package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherAdviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor // Sử dụng Lombok để inject các Service qua Constructor
public class WeatherFacadeService {

    private final WeatherService weatherService;
    private final AiAdvisorService aiAdvisorService;

    public Mono<WeatherAdviceResponse> getWeatherWithAdvice(String city) {
        return weatherService.getCurrentWeather(city) // Bước 1: Gọi API thời tiết
                .flatMap(weatherData ->
                        aiAdvisorService.getAdviceFromWeather(weatherData.getCurrent()) // Bước 2: Lấy dữ liệu đó gọi sang AI
                                .map(advice -> new WeatherAdviceResponse( // Bước 3: Trộn kết quả
                                        weatherData.getLocation().getName(),
                                        weatherData.getCurrent().getTempC(),
                                        weatherData.getCurrent().getCondition().getText(),
                                        advice
                                ))
                );
    }
}
