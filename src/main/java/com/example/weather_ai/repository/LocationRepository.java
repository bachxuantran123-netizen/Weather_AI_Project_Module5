package com.example.weather_ai.repository;

import com.example.weather_ai.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Custom query: Kiểm tra xem thành phố đã có trong database chưa trước khi lưu mới, tránh duplicate
    Optional<Location> findByCityName(String cityName);
}