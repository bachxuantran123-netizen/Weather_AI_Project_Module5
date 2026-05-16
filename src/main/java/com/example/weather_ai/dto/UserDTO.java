package com.example.weather_ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[35789][0-9]{8}$", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters long and contain both letters and numbers")
    private String password;
}