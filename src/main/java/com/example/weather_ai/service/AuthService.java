package com.example.weather_ai.service;

import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String rawPassword) {
        if (accountRepository.existsByUsername(username)) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        Account newAccount = new Account();
        newAccount.setUsername(username);
        // Bắt buộc mã hóa mật khẩu trước khi lưu
        newAccount.setPassword(passwordEncoder.encode(rawPassword));

        accountRepository.save(newAccount);
    }
}