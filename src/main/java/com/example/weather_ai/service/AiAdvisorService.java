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

    public Mono<AiAdviceDto> getAdviceFromWeather(WeatherApiResponse weatherData) {
        WeatherApiResponse.CurrentDto currentData = weatherData.getCurrent();

        // Xử lý chuỗi cảnh báo thiên tai (Nếu có)
        StringBuilder alertsText = new StringBuilder();
        if (weatherData.getAlerts() != null && weatherData.getAlerts().getAlert() != null && !weatherData.getAlerts().getAlert().isEmpty()) {
            alertsText.append("\n⚠️ ĐANG CÓ CẢNH BÁO THỜI TIẾT KHẨN CẤP TẠI KHU VỰC NÀY:\n");
            for (WeatherApiResponse.AlertItemDto alert : weatherData.getAlerts().getAlert()) {
                alertsText.append("- ").append(alert.getHeadline())
                        .append(" (Mức độ: ").append(alert.getSeverity()).append(")\n");
            }
        }

        String prompt = String.format("""
                Thời tiết đang là %s, nhiệt độ %s độ C, chỉ số UV %s. %s
                Hãy đóng vai chuyên gia thời tiết đưa ra lời khuyên. 
                🚨 NẾU CÓ CẢNH BÁO THỜI TIẾT KHẨN CẤP BÊN TRÊN: Hãy BỎ QUA các lời khuyên thông thường, ƯU TIÊN đưa ra lời khuyên sinh tồn, an toàn tính mạng trong phần 'warnings' và 'advice'.
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
                currentData.getUv(),
                alertsText.toString()
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
                        return new AiAdviceDto("LỖI JSON: Không thể phân tích phản hồi từ AI.", List.of(), List.of());
                    }
                })
                .onErrorResume(e -> Mono.just(new AiAdviceDto(
                        "Hệ thống AI đang bận. Vui lòng chú ý an toàn nếu thời tiết xấu.",
                        List.of("Trang phục phù hợp với nhiệt độ"),
                        List.of("Chú ý an toàn khi di chuyển")
                )));
    }
}