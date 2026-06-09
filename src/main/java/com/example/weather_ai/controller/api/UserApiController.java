package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.ApiResponse;
import com.example.weather_ai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            // Lấy username từ Token Bearer đã được Spring Security giải mã
            String username = authentication.getName();
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thành công", authService.getUserProfile(username)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}