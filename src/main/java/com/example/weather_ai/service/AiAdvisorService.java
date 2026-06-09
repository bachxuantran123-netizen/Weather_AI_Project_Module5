package com.example.weather_ai.service;

import com.example.weather_ai.dto.AiAdviceDto;
import com.example.weather_ai.dto.WeatherApiResponse;
import com.example.weather_ai.dto.GeminiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public AiAdvisorService(
            @Value("${ai.base-url}") String fullUrl,
            @Value("${ai.api-key}") String aiApiKey) {
        this.webClient = WebClient.create();
        this.aiApiKey = aiApiKey.trim();
        this.fullUrl = fullUrl.trim();
        this.objectMapper = new ObjectMapper();
    }

    public Mono<AiAdviceDto> getAdviceFromWeather(WeatherApiResponse.CurrentDto currentData) {
        String prompt = String.format("""
                Thời tiết đang là %s, nhiệt độ %s độ C, chỉ số UV %s. Hãy đóng vai chuyên gia thời tiết đưa ra lời khuyên.
                BẮT BUỘC trả về dữ liệu dưới định dạng JSON, KHÔNG bọc trong markdown block.
                Cấu trúc JSON yêu cầu:
                {
                    "advice": "lời khuyên chung dưới 30 chữ",
                    "items_to_bring": ["món đồ 1", "món đồ 2"],
                    "warnings": ["cảnh báo 1", "cảnh báo 2"]
                }
                """,
                currentData.getCondition().getText(),
                currentData.getTempC(),
                currentData.getUv()
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );

        return this.webClient.post()
                .uri(this.fullUrl)
                .header("x-goog-api-key", this.aiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .map(response -> {
                    try {
                        String jsonString = response.getCandidates().get(0).getContent().getParts().get(0).getText();
                        return objectMapper.readValue(jsonString, AiAdviceDto.class);
                    } catch (Exception e) {
                        return new AiAdviceDto("LỖI: Không thể phân tích phản hồi từ AI.", List.of(), List.of());
                    }
                })
                .onErrorResume(e -> Mono.just(new AiAdviceDto("🚨 LỖI TỪ GEMINI: " + e.getMessage(), List.of(), List.of())));
    }
}