package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.JwtResponse;
import com.example.weather_ai.dto.LoginRequest;
import com.example.weather_ai.service.AuthService;
import com.example.weather_ai.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest request) {
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        try {
            // Spring Security sẽ TỰ ĐỘNG gọi CustomUserDetailsService để lấy user và tự động check mật khẩu mã hóa
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Nếu không lỗi tức là pass, lưu vào Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT và trả về
            String jwt = jwtUtils.generateJwtToken(request.getUsername());
            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Tài khoản hoặc mật khẩu không chính xác");
        }
    }
}