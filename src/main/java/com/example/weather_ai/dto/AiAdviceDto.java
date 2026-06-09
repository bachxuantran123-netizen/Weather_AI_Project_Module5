package com.example.weather_ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiAdviceDto {
    private String advice;

    // Đảm bảo map đúng key "items_to_bring" từ JSON
    @JsonProperty("items_to_bring")
    private List<String> itemsToBring;

    private List<String> warnings;
}