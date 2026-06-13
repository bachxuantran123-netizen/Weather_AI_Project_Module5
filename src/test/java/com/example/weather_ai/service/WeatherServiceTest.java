package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    private WeatherService weatherService;

    @Mock
    private ExchangeFunction exchangeFunction;

    @BeforeEach
    void setUp() {
        // Xây dựng một WebClient giả sử dụng ExchangeFunction bị mock
        WebClient mockWebClient = WebClient.builder()
                .exchangeFunction(exchangeFunction)
                .build();

        weatherService = new WeatherService("http://dummy-weather.api", "dummyKey");

        try {
            // Thay thế webClient bên trong WeatherService bằng WebClient đã bị mock
            java.lang.reflect.Field field = WeatherService.class.getDeclaredField("webClient");
            field.setAccessible(true);
            field.set(weatherService, mockWebClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Lấy thời tiết thành công - Trả về WeatherApiResponse hợp lệ")
    void givenValidCity_whenGetWeatherForecast_thenReturnWeatherApiResponse() {
        // Given
        String city = "Hanoi";

        // 1. Tạo chuỗi JSON thô (raw JSON) giả lập phản hồi chuẩn từ WeatherAPI
        String mockJsonResponse = """
                {
                    "location": {
                        "name": "Hanoi",
                        "country": "Vietnam"
                    },
                    "current": {
                        "temp_c": 25.0,
                        "uv": 5.0,
                        "condition": {
                            "text": "Sunny"
                        }
                    }
                }
                """;

        // 2. Build ClientResponse nhận vào chuỗi JSON vừa tạo
        ClientResponse mockClientResponse = ClientResponse.create(org.springframework.http.HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(mockJsonResponse)
                .build();

        // 3. Ép ExchangeFunction trả về mockClientResponse khi WebClient thực hiện call API
        given(exchangeFunction.exchange(any())).willReturn(Mono.just(mockClientResponse));

        // When
        Mono<WeatherApiResponse> result = weatherService.getWeatherForecast(city);

        // Then (Kiểm tra xem JSON có được parse thành công ra DTO không)
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getLocation().getName().equals("Hanoi") &&
                                response.getCurrent().getTempC() == 25.0)
                .verifyComplete();
    }
}