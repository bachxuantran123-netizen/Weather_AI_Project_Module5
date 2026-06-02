package com.example.weather_ai.service;

import com.example.weather_ai.entity.Account;
import com.example.weather_ai.repository.AccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + username));

        // Nạp Role từ DB vào Spring Security (bắt buộc phải có tiền tố ROLE_)
        List<SimpleGrantedAuthority> authorities =
                java.util.Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + account.getRole().toUpperCase())
                );

        return new User(
                account.getUsername(),
                account.getPassword(),
                account.isActive(),
                true,
                true,
                true,
                authorities
        );
    }
}