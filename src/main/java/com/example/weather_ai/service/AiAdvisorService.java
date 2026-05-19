package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherApiResponse;
import com.example.weather_ai.dto.GeminiResponse;
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

        this.webClient = WebClient.create();
        this.aiApiKey = aiApiKey.trim();
        this.fullUrl = fullUrl.trim();
    }

    public Mono<String> getAdviceFromWeather(WeatherApiResponse.CurrentDto currentData) {
        String prompt = String.format(
                "Thời tiết đang là %s, nhiệt độ %s độ C, chỉ số UV %s. Hãy đóng vai chuyên gia thời tiết khuyên 1 câu ngắn gọn (dưới 30 chữ) nên mang đồ gì ra đường.",
                currentData.getCondition().getText(), currentData.getTempC(), currentData.getUv()
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        return this.webClient.post()
                .uri(this.fullUrl)
                .header("x-goog-api-key", this.aiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GeminiResponse.class) // Hứng bằng DTO chuẩn
                .map(response -> {
                    try {
                        return response.getCandidates().get(0).getContent().getParts().get(0).getText();
                    } catch (Exception e) {
                        return "Không thể phân tích phản hồi từ AI.";
                    }
                })
                .onErrorResume(e -> Mono.just("🚨 LỖI TỪ GEMINI: " + e.getMessage()));
    }
}