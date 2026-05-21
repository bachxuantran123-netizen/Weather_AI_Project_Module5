package com.example.weather_ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WeatherAiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherAiApplication.class, args);
    }

}
