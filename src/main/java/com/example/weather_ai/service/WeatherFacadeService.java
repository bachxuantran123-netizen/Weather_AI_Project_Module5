package com.example.weather_ai.service;

import com.example.weather_ai.dto.WeatherAdviceResponse;
import com.example.weather_ai.dto.WeatherApiResponse;
import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
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
    private final CalendarService calendarService;
    private final AccountRepository accountRepository;

    @Cacheable(
            value = "weatherCache",
            key = "#city + '_' + (#username != null ? #username : 'guest')",
            unless = "#result.aiAdvice == null || #result.aiAdvice.advice == null || #result.aiAdvice.advice.contains('LỖI')"
    )
    public WeatherAdviceResponse getWeatherWithAdvice(String city, String username) {
        String events = "";
        if (username != null) {
            Account account = accountRepository.findByUsername(username).orElse(null);
            if (account != null) {
                events = calendarService.getTodaysEvents(account);
            }
        }
        final String finalCalendarEvents = events;

        return weatherService.getWeatherForecast(city)
                .flatMap(weatherData -> {
                    WeatherApiResponse.CurrentDto current = weatherData.getCurrent();
                    WeatherApiResponse.ForecastdayDto today = weatherData.getForecast().getForecastday().get(0);

                    LocalDateTime now = LocalDateTime.now();
                    String compareString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00"));

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

                    return aiAdvisorService.getAdviceFromWeather(weatherData, finalCalendarEvents)
                            .map(advice -> new WeatherAdviceResponse(
                                    weatherData.getLocation().getName(),
                                    weatherData.getLocation().getLat(),
                                    weatherData.getLocation().getLon(),
                                    current.getTempC(),
                                    current.getCondition().getText(),
                                    today.getDay().getMaxtempC(),
                                    today.getDay().getMintempC(),
                                    current.getUv(),
                                    current.getHumidity(),
                                    current.getWindKph(),
                                    current.getVisKm(),
                                    hourlyForecasts,
                                    advice
                            ));
                })
                .block();
    }
}