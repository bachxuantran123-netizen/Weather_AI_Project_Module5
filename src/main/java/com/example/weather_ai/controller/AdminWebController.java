package com.example.weather_ai.controller;

import com.example.weather_ai.entity.AccountLocation;
import com.example.weather_ai.repository.AccountLocationRepository;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.repository.SearchHistoryRepository;
import com.example.weather_ai.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final AccountRepository accountRepository;
    private final AccountLocationRepository accountLocationRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final LocationService locationService;

    @GetMapping("/dashboard")
    public String showDashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        long activeUsers = accountRepository.count();

        Page<AccountLocation> locationPage = locationService.getPaginatedSavedLocations(page, size);

        // 1. Lấy TỔNG SỐ THẬT từ bảng Lịch sử
        long totalRequests = searchHistoryRepository.count();

        // 2. Xử lý dữ liệu vẽ Chart (7 ngày gần nhất)
        List<Object[]> stats = searchHistoryRepository.countRequestsByDayLast7Days();
        List<String> chartLabels = new ArrayList<>();
        List<Long> chartData = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (Object[] row : stats) {
            Object dateObj = row[0];
            LocalDate localDate;
            if (dateObj instanceof java.time.LocalDate) {
                localDate = (java.time.LocalDate) dateObj;
            } else if (dateObj instanceof java.sql.Date) {
                localDate = ((java.sql.Date) dateObj).toLocalDate();
            } else {
                localDate = LocalDate.parse(dateObj.toString());
            }

            Long count = ((Number) row[1]).longValue();

            chartLabels.add(localDate.format(formatter));
            chartData.add(count);
        }

        model.addAttribute("pageTitle", "Weather_AI | Admin Dashboard");
        model.addAttribute("welcomeMessage", "Chào mừng Admin!");
        model.addAttribute("activeUsers", activeUsers);

        model.addAttribute("locationPage", locationPage);

        model.addAttribute("totalRequests", totalRequests);
        model.addAttribute("aiAdvicesGenerated", totalRequests);

        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String showUsersManager(Model model) {
        List<com.example.weather_ai.entity.Account> users = accountRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Weather_AI | Quản lý User");
        return "admin/users";
    }
}