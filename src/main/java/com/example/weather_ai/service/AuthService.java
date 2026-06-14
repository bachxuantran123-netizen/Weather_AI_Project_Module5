package com.example.weather_ai.service;

import com.example.weather_ai.dto.*;
import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;

    public void register(String username, String rawPassword) {
        if (accountRepository.existsByUsername(username)) {
            throw new RuntimeException("Error: Username is already taken!");
        }
        Account newAccount = new Account();
        newAccount.setUsername(username);
        newAccount.setPassword(passwordEncoder.encode(rawPassword));
        newAccount.setRole("USER");
        newAccount.setActive(true);

        accountRepository.save(newAccount);
    }

    public JwtResponse login(LoginRequest request) {
        Account account = accountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Sai tên đăng nhập hoặc mật khẩu"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("Sai tên đăng nhập hoặc mật khẩu");
        }

        if (!account.isActive()) {
            throw new RuntimeException("Tài khoản đã bị khóa!");
        }

        String accessToken = jwtUtils.generateTokenFromUsername(account.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(account.getUsername());

        // Lưu Refresh Token vào Redis với TTL là 7 ngày
        redisTemplate.opsForValue().set(
                "RT_" + account.getUsername(),
                refreshToken,
                jwtUtils.getRefreshExpirationMs(),
                TimeUnit.MILLISECONDS
        );

        return new JwtResponse(accessToken, refreshToken, account.getUsername(), account.getRole());
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // 1. Kiểm tra tính hợp lệ của token
        if (!jwtUtils.validateJwtToken(requestRefreshToken)) {
            throw new RuntimeException("Refresh Token không hợp lệ hoặc đã hết hạn!");
        }

        String username = jwtUtils.getUserNameFromJwtToken(requestRefreshToken);

        // 2. So sánh với token đang lưu trong Redis
        String redisToken = redisTemplate.opsForValue().get("RT_" + username);
        if (redisToken == null || !redisToken.equals(requestRefreshToken)) {
            throw new RuntimeException("Refresh Token không tồn tại hoặc đã bị thu hồi!");
        }

        // 3. Cấp phát cặp Token mới
        String newAccessToken = jwtUtils.generateTokenFromUsername(username);
        String newRefreshToken = jwtUtils.generateRefreshToken(username);

        // 4. Cập nhật lại Refresh Token mới vào Redis
        redisTemplate.opsForValue().set(
                "RT_" + username,
                newRefreshToken,
                jwtUtils.getRefreshExpirationMs(),
                TimeUnit.MILLISECONDS
        );

        return new TokenRefreshResponse(newAccessToken, newRefreshToken);
    }

    public UserProfileResponse getUserProfile(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        return new UserProfileResponse(
                account.getId(),
                account.getUsername(),
                account.getRole(),
                account.isActive()
        );
    }

    public void updateDeviceToken(String username, String token) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        account.setFcmDeviceToken(token);
        accountRepository.save(account);
    }
}