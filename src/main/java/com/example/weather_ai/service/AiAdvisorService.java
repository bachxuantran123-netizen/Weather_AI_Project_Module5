package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AiAdvisorService {

    private final WebClient webClient;
    private final String aiApiKey;

    public AiAdvisorService(
            WebClient.Builder webClientBuilder,
            @Value("${ai.base-url}") String baseUrl,
            @Value("${ai.api-key}") String aiApiKey) {

        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.aiApiKey = aiApiKey;
    }

    // Hàm này sẽ nhận dữ liệu thời tiết thô và yêu cầu AI phân tích
    public Mono<String> getAdviceFromWeather(WeatherApiResponse.CurrentDto currentData) {
        // Tạo câu lệnh (Prompt) để ra lệnh cho AI
        String prompt = String.format(
                "Thời tiết hiện tại đang là %s, nhiệt độ %s độ C, chỉ số UV là %s. " +
                        "Hãy đóng vai một chuyên gia sức khỏe, đưa ra lời khuyên ngắn gọn (dưới 50 từ) " +
                        "về việc nên mang theo gì khi ra đường.",
                currentData.getCondition().getText(),
                currentData.getTempC(),
                currentData.getUv()
        );

        // TODO: Gửi prompt này lên AI thông qua WebClient và map JSON trả về.
        // Tạm thời trả về Mock Data để ráp nối luồng (Pipeline) trước.
        return Mono.just("AI Advice (Mock): " + prompt);
    }
}