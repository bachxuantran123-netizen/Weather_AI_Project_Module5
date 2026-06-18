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

    public Mono<AiAdviceDto> getAdviceFromWeather(WeatherApiResponse weatherData, String calendarEvents) {
        WeatherApiResponse.CurrentDto currentData = weatherData.getCurrent();

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
            
            📅 THÔNG TIN LỊCH TRÌNH:
            %s
            
            Hãy đóng vai một trợ lý ảo thông minh đưa ra lời khuyên cho tôi. 
            🚨 NẾU CÓ CẢNH BÁO THỜI TIẾT KHẨN CẤP: BỎ QUA các lời khuyên thông thường, ƯU TIÊN đưa ra chỉ dẫn sinh tồn, an toàn tính mạng.
            🚨 LƯU Ý VỀ LỊCH TRÌNH: Hãy đọc lịch trình của tôi bên trên. Tư vấn giúp tôi sắp xếp thời gian di chuyển, mang theo đồ đạc phù hợp với sự kiện, hoặc đề xuất dời lịch nếu thời tiết quá khắc nghiệt.
            
            BẮT BUỘC trả về dữ liệu dưới định dạng JSON, KHÔNG bọc trong markdown block.
            Cấu trúc JSON yêu cầu:
            {
                "advice": "lời khuyên chung dưới 40 chữ (kết hợp phân tích cả thời tiết và lịch trình)",
                "items_to_bring": ["món đồ 1", "món đồ 2"],
                "warnings": ["cảnh báo 1", "cảnh báo 2"],
                "is_severe_disaster": true/false
            }
            """,
                currentData.getCondition().getText(),
                currentData.getTempC(),
                currentData.getUv(),
                alertsText.toString(),
                (calendarEvents != null && !calendarEvents.isBlank()) ? calendarEvents : "Tôi không có lịch trình nào đặc biệt hôm nay."
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
                        return new AiAdviceDto("LỖI JSON: Không thể phân tích phản hồi từ AI.", List.of(), List.of(), false);
                    }
                })
                .onErrorResume(e -> Mono.just(new AiAdviceDto(
                        "Hệ thống AI đang bận. Vui lòng chú ý an toàn nếu thời tiết xấu.",
                        List.of("Trang phục phù hợp với nhiệt độ"),
                        List.of(),
                        false
                )));
    }
}