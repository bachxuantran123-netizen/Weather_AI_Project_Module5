package com.example.weather_ai.mapper;

import com.example.weather_ai.dto.CreateUserDTO;
import com.example.weather_ai.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;

    public UserMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public User toEntity(CreateUserDTO dto) {
        return modelMapper.map(dto, User.class);
    }
}