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

    @JsonProperty("items_to_bring")
    private List<String> itemsToBring;

    private List<String> warnings;

    @JsonProperty("is_severe_disaster")
    private boolean isSevereDisaster;
}