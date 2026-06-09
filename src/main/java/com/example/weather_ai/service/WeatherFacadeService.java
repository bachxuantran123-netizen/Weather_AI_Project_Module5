package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherAdviceResponse;
import com.example.weather_ai.dto.WeatherApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherFacadeService {

    private final WeatherService weatherService;
    private final AiAdvisorService aiAdvisorService;

    // KHÔNG lưu cache nếu chuỗi aiAdvice có chứa từ "LỖI"
    @Cacheable(
            value = "weatherCache",
            key = "#city",
            unless = "#result.aiAdvice == null || #result.aiAdvice.advice == null || #result.aiAdvice.advice.contains('LỖI')"
    )
    public WeatherAdviceResponse getWeatherWithAdvice(String city) {

        // 1. Đổi sang gọi hàm lấy Dự báo 3 ngày
        return weatherService.getWeatherForecast(city)
                .flatMap(weatherData -> {
                    WeatherApiResponse.CurrentDto current = weatherData.getCurrent();
                    WeatherApiResponse.ForecastdayDto today = weatherData.getForecast().getForecastday().get(0);

                    // 2. THUẬT TOÁN TÍNH TOÁN GIỜ THỰC TẾ
                    // Lấy giờ hiện tại format thành dạng chuỗi chuẩn API (Ví dụ: "2023-10-25 10:00")
                    LocalDateTime now = LocalDateTime.now();
                    String compareString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));

                    // Trải phẳng (flatMap) mảng giờ của 3 ngày ra, sau đó lọc lấy từ giờ hiện tại trở đi
                    List<WeatherAdviceResponse.HourlyForecastDto> hourlyForecasts = weatherData.getForecast().getForecastday().stream()
                            .flatMap(day -> day.getHour().stream())
                            .filter(h -> h.getTime().compareTo(compareString) >= 0)
                            .limit(24)
                            .map(h -> new WeatherAdviceResponse.HourlyForecastDto(
                                    h.getTime().substring(11),
                                    h.getTempC(),
                                    "https:" + h.getCondition().getIcon()
                            ))
                            .toList();

                    // 3. MAP TOÀN BỘ DỮ LIỆU ĐỂ TRẢ VỀ CHO APP MOBILE
                    return aiAdvisorService.getAdviceFromWeather(current)
                            .map(advice -> new WeatherAdviceResponse(
                                    weatherData.getLocation().getName(),
                                    current.getTempC(),
                                    current.getCondition().getText(),
                                    today.getDay().getMaxtempC(),     // Nhiệt độ cao nhất
                                    today.getDay().getMintempC(),     // Nhiệt độ thấp nhất
                                    current.getUv(),                  // Chỉ số UV
                                    current.getHumidity(),            // Độ ẩm
                                    current.getWindKph(),             // Sức gió
                                    current.getVisKm(),               // Tầm nhìn
                                    hourlyForecasts,                  // Danh sách 24h tới
                                    advice                            // Lời khuyên từ AI
                            ));
                })
                .block();
    }
}