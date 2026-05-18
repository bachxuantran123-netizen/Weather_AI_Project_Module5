package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class AiAdvisorService {

    private final WebClient webClient;
    private final String aiApiKey;
    private final String fullUrl;

    public AiAdvisorService(
            @Value("${ai.base-url}") String fullUrl,
            @Value("${ai.api-key}") String aiApiKey) {

        // Tạo WebClient trống, không gán BaseURL mặc định để tránh xung đột
        this.webClient = WebClient.create();
        this.aiApiKey = aiApiKey.trim();
        this.fullUrl = fullUrl.trim();
    }

    public Mono<String> getAdviceFromWeather(WeatherApiResponse.CurrentDto currentData) {
        String prompt = String.format(
                "Thời tiết đang là %s, nhiệt độ %s độ C, chỉ số UV %s. Hãy khuyên 1 câu ngắn gọn nên mang đồ gì.",
                currentData.getCondition().getText(), currentData.getTempC(), currentData.getUv()
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        // Nối URL đầy đủ với API Key
        String finalUrl = this.fullUrl + "?key=" + this.aiApiKey;

        return this.webClient.post()
                .uri(finalUrl)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(rootNode -> {
                    try {
                        return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
                    } catch (Exception e) {
                        return "Không thể phân tích phản hồi từ AI.";
                    }
                })
                .onErrorResume(e -> Mono.just("🚨 LỖI TỪ GEMINI: " + e.getMessage()));
    }
}