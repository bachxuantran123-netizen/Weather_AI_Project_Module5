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

        Page<AccountLocation> locationPage = locationService.getPaginatedSavedLocations(page, size);

        model.addAttribute("pageTitle", "Weather_AI | Admin Dashboard");
        model.addAttribute("welcomeMessage", "Chào mừng Admin!");
        model.addAttribute("locationPage", locationPage);

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