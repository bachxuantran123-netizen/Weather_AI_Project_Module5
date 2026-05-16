package com.example.weather_ai.service;

import com.example.weather_ai.dto.CreateUserDTO;
import com.example.weather_ai.entity.User;
import com.example.weather_ai.mapper.UserMapper;
import com.example.weather_ai.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerNewUser(CreateUserDTO dto) {
        User user = userMapper.toEntity(dto);

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");

        userRepository.save(user);
    }







}
