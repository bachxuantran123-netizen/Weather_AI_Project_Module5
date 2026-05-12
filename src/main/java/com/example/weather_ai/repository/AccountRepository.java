package com.example.weather_ai.repository;

import com.example.weather_ai.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Custom query: Giúp Service dễ dàng tìm người dùng lúc Đăng nhập
    Optional<Account> findByUsername(String username);
}
