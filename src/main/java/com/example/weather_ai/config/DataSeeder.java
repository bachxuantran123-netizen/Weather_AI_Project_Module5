package com.example.weather_ai.config;

import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!accountRepository.existsByUsername("admin")) {
            Account admin = new Account();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("ADMIN");
            admin.setActive(true);

            accountRepository.save(admin);
            System.out.println("=================================================");
            System.out.println("[SYSTEM_INIT] ĐÃ TẠO TÀI KHOẢN ADMIN MẶC ĐỊNH");
            System.out.println("=================================================");
        }
    }
}