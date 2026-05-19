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

    public Mono<WeatherApiResponse> getCurrentWeather(String city) {
        String finalUrl = this.baseUrl + "/current.json?key=" + this.apiKey + "&q=" + city + "&aqi=no";

        return this.webClient.get()
                .uri(finalUrl)
                .retrieve()
                .bodyToMono(WeatherApiResponse.class)
                .onErrorMap(e -> new RuntimeException("🚨 LỖI TỪ WEATHER API: " + e.getMessage()));
    }
}