package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {

    private final WebClient webClient;
    private final String apiKey;
    private final String baseUrl;

    public WeatherService(
            @Value("${weatherapi.base-url}") String baseUrl,
            @Value("${weatherapi.key}") String apiKey) {
        this.webClient = WebClient.create(baseUrl.trim());
        this.apiKey = apiKey.trim();
        this.baseUrl = baseUrl.trim();
    }

    // ĐỔI TÊN HÀM VÀ ĐƯỜNG DẪN URL
    public Mono<WeatherApiResponse> getWeatherForecast(String city) {
        // Đổi từ /current.json sang /forecast.json và thêm &days=3
        String finalUrl = this.baseUrl + "/forecast.json?key=" + this.apiKey + "&q=" + city + "&days=3&aqi=no&alerts=no";

        return this.webClient.get()
                .uri(finalUrl)
                .retrieve()
                .bodyToMono(WeatherApiResponse.class)
                .onErrorMap(e -> new RuntimeException("🚨 LỖI TỪ WEATHER API: " + e.getMessage()));
    }
}