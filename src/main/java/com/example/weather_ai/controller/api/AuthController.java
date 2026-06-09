package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.ApiResponse;
import com.example.weather_ai.dto.JwtResponse;
import com.example.weather_ai.dto.LoginRequest;
import com.example.weather_ai.service.AuthService;
import com.example.weather_ai.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
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
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;

    public AuthController(AuthService authService, JwtUtils jwtUtils, AuthenticationManager authenticationManager,  StringRedisTemplate redisTemplate) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody LoginRequest request) {
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@RequestBody LoginRequest request) {
        try {
            // Spring Security sẽ TỰ ĐỘNG gọi CustomUserDetailsService để lấy user và tự động check mật khẩu mã hóa
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Nếu không lỗi tức là pass, lưu vào Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT và trả về
            String jwt = jwtUtils.generateJwtToken(request.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Login successful!", new JwtResponse(jwt)));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("Tài khoản hoặc mật khẩu không chính xác"));
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
}