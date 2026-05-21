package com.example.weather_ai.repository;

import com.example.weather_ai.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    // Đếm tổng request theo từng ngày (7 ngày gần nhất)
    @Query(value = "SELECT DATE(created_at) as log_date, COUNT(*) as total_requests " +
            "FROM search_history " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY DATE(created_at) ASC", nativeQuery = true)
    List<Object[]> countRequestsByDayLast7Days();
}