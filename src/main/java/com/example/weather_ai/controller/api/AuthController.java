package com.example.weather_ai.controller.api;

import com.example.weather_ai.dto.JwtResponse;
import com.example.weather_ai.dto.LoginRequest;
import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.service.AuthService;
import com.example.weather_ai.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // constructor injection
    public AuthController(AuthService authService, AccountRepository accountRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authService = authService;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest request) { // Dùng DTO
        authService.register(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        // Tìm user, kiểm tra mật khẩu
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Nếu hợp lệ, tạo JWT
        String jwt = jwtUtils.generateJwtToken(account.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwt)); // Dùng DTO trả về token
    }
}
