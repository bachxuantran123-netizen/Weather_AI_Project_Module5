package com.example.weather_ai.repository;

import com.example.weather_ai.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber")
    Optional<User> findByPhoneNumber(String phoneNumber);

}