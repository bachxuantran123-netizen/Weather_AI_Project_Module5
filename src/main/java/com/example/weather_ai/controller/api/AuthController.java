package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.ApiResponse;
import com.example.weather_ai.dto.JwtResponse;
import com.example.weather_ai.dto.LoginRequest;
import com.example.weather_ai.dto.TokenRefreshRequest;
import com.example.weather_ai.service.AuthService;
import com.example.weather_ai.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody LoginRequest request) {
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!", null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            JwtResponse jwtResponse = authService.login(request);

            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công!", jwtResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String jwt = headerAuth.substring(7).trim();

            Date expiration = jwtUtils.getExpirationDateFromJwtToken(jwt);
            long remainingTime = expiration.getTime() - System.currentTimeMillis();

            if (remainingTime > 0) {
                redisTemplate.opsForValue().set("BL_" + jwt, "logout", remainingTime, TimeUnit.MILLISECONDS);
                System.out.println("Đã đưa Token vào Blacklist Redis: BL_" + jwt);
            }
            return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công! Token đã bị vô hiệu hóa.", null));
        }

        return ResponseEntity.badRequest().body(ApiResponse.error("Yêu cầu không hợp lệ hoặc thiếu Token."));
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Làm mới token thành công", authService.refreshToken(request)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}