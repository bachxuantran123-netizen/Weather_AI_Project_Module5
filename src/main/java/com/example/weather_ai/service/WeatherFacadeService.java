package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherAdviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherFacadeService {

    private final WeatherService weatherService;
    private final AiAdvisorService aiAdvisorService;

    // Kích hoạt Cache
    @Cacheable(value = "weatherCache", key = "#city")
    public WeatherAdviceResponse getWeatherWithAdvice(String city) {
        return weatherService.getCurrentWeather(city)
                .flatMap(weatherData ->
                        aiAdvisorService.getAdviceFromWeather(weatherData.getCurrent())
                                .map(advice -> new WeatherAdviceResponse(
                                        weatherData.getLocation().getName(),
                                        weatherData.getCurrent().getTempC(),
                                        weatherData.getCurrent().getCondition().getText(),
                                        advice
                                ))
                )
                .block(); // Ép chạy đồng bộ để hứng kết quả thật lưu vào Redis
    }
}