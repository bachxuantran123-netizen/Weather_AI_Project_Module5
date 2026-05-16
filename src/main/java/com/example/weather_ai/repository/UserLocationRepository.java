package com.example.weather_ai.repository;

import com.example.weather_ai.entity.AccountLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserLocationRepository extends JpaRepository<AccountLocation, Long> {
    // Custom query: Lấy toàn bộ danh sách địa điểm (kèm biệt danh) mà một user đang theo dõi
    List<AccountLocation> findByAccountId(Long accountId);
}
