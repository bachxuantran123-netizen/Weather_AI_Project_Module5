package com.example.weather_ai.controller;

import com.example.weather_ai.entity.Account;
import com.example.weather_ai.entity.AccountLocation;
import com.example.weather_ai.entity.CommunityReport;
import com.example.weather_ai.entity.SearchHistory;
import com.example.weather_ai.repository.AccountLocationRepository;
import com.example.weather_ai.repository.AccountRepository;
import com.example.weather_ai.repository.CommunityReportRepository;
import com.example.weather_ai.repository.SearchHistoryRepository;
import com.example.weather_ai.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final AccountRepository accountRepository;
    private final AccountLocationRepository accountLocationRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final LocationService locationService;
    private final CommunityReportRepository communityReportRepository;

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
        List<Account> users = accountRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Weather_AI | Quản lý User");
        return "admin/users";
    }

    @GetMapping("/locations")
    public String showLocations(Model model) {
        List<AccountLocation> locations = accountLocationRepository.findAll();
        model.addAttribute("locations", locations);
        model.addAttribute("pageTitle", "Weather_AI | Địa điểm yêu thích");
        return "admin/locations";
    }

    @GetMapping("/ai-history")
    public String showAiHistory(Model model) {
        List<SearchHistory> histories = searchHistoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("histories", histories);
        model.addAttribute("pageTitle", "Weather_AI | Lịch sử AI");
        return "admin/ai-history";
    }

    @GetMapping("/community")
    public String showCommunityReports(Model model) {
        List<CommunityReport> reports = communityReportRepository.findAll(Sort.by(Sort.Direction.DESC, "reportTime"));
        model.addAttribute("reports", reports);
        model.addAttribute("pageTitle", "Weather_AI | Báo cáo Cộng đồng");
        return "admin/community";
    }
}