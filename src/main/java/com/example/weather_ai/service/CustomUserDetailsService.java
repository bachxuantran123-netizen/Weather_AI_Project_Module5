package com.example.weather_ai.service;

import com.example.weather_ai.entity.User;
import com.example.weather_ai.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String phoneNumberInput) throws UsernameNotFoundException {

        // Dùng số điện thoại để chọc xuống Database tìm kiếm
        User user = userRepository.findByPhoneNumber(phoneNumberInput)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy số điện thoại: " + phoneNumberInput));

        // Nếu tìm thấy, nạp dữ liệu vào đối tượng của Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPhoneNumber())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}