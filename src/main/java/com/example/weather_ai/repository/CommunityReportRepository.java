package com.example.weather_ai.repository;

import com.example.weather_ai.entity.CommunityReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {
    List<CommunityReport> findByCityNameOrderByReportTimeDesc(String cityName);
}