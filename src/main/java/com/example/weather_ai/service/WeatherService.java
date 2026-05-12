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

    public WeatherService(
            WebClient.Builder webClientBuilder,
            @Value("${weatherapi.base-url}") String baseUrl,
            @Value("${weatherapi.key}") String apiKey) {

        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    // Phương thức trả về Mono (Bất đồng bộ)
    public Mono<WeatherApiResponse> getCurrentWeather(String city) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/current.json")
                        .queryParam("key", apiKey)
                        .queryParam("q", city)
                        .queryParam("aqi", "no") // Tạm thời tắt lấy chỉ số bụi mịn
                        .build())
                .retrieve()
                .bodyToMono(WeatherApiResponse.class);
    }
}