package com.example.weather_ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    // Mở Link Này Để Chạy Nhé AE http://localhost:8080/admin/dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Gắn dữ liệu tạm thời để giao diện Thymeleaf hiển thị
        model.addAttribute("pageTitle", "Hệ thống Quản trị Thời tiết AI");
        model.addAttribute("welcomeMessage", "Chào mừng Admin!");

        // Demo data: provide sample stats and accounts so dashboard shows meaningful content.
        var stats = Map.of(
                "totalUsers", 128,
                "activeToday", 34,
                "openAlerts", 3,
                "aiRequests", 542
        );
        model.addAttribute("stats", stats);

        // Use a small local DTO to display accounts in the demo table
        record DisplayAccount(Long id, String name, String email, boolean active) {}

        List<DisplayAccount> accounts = List.of(
                new DisplayAccount(1L, "Nguyễn Văn A", "a@example.com", true),
                new DisplayAccount(2L, "Trần Thị B", "b@example.com", false),
                new DisplayAccount(3L, "Lê Văn C", "c@example.com", true)
        );
        model.addAttribute("accounts", accounts);

        // TODO (Dành cho thành viên nhóm):
        // 1. Tiêm (Inject) AccountRepository vào đây và thay thế dữ liệu mẫu bằng dữ liệu thực từ DB.
        // 2. Thêm phân trang nếu danh sách lớn.
        return "dashboard"; // Spring Boot sẽ tìm file dashboard.html trong thư mục templates
    }
}
