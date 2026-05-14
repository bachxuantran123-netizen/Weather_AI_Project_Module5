package com.example.weather_ai.repository;

import com.example.weather_ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User,Long> {
}
