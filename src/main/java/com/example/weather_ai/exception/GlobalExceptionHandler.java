package com.example.weather_ai.exception;

import com.example.weather_ai.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi khi gọi API bên ngoài (WeatherAPI, Gemini) bị thất bại
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponse<Object>> handleWebClientExceptions(WebClientResponseException ex) {
        return new ResponseEntity<>(ApiResponse.error("Lỗi tích hợp hệ thống bên thứ 3: " + ex.getMessage()), ex.getStatusCode());
    }

    // Bắt các lỗi Runtime chung của hệ thống (NullPointer, IllegalArgument...)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalExceptions(Exception ex) {
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
