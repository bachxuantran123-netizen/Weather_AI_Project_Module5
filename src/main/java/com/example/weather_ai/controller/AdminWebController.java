package com.example.weather_ai.controller;

import com.example.weather_ai.entity.AccountLocation;
import com.example.weather_ai.repository.AccountLocationRepository;
import com.example.weather_ai.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final AccountRepository accountRepository;
    private final AccountLocationRepository accountLocationRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // 1. Truy vấn dữ liệu THẬT từ Database
        long activeUsers = accountRepository.count();
        List<AccountLocation> recentLocations = accountLocationRepository.findAll();

        // 2. Đẩy dữ liệu sang cho file HTML
        model.addAttribute("pageTitle", "Weather_AI | Admin Dashboard");
        model.addAttribute("welcomeMessage", "Chào mừng Admin!");

        // Dữ liệu động
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("recentLocations", recentLocations);

        // Dữ liệu giả lập (Sau này nếu có bảng Log API thì thay bằng số thật)
        model.addAttribute("totalRequests", 1542);
        model.addAttribute("aiAdvicesGenerated", 980);

        return "admin/dashboard";
    }
}
