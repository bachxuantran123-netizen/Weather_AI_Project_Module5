package com.example.weather_ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    // Mở Link Này Để Chạy Nhé AE http://localhost:8080/admin/dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Gắn dữ liệu tạm thời để giao diện Thymeleaf hiển thị
        model.addAttribute("pageTitle", "Hệ thống Quản trị Thời tiết AI");
        model.addAttribute("welcomeMessage", "Chào mừng Admin!");

        // TODO (Dành cho thành viên nhóm):
        // 1. Tiêm (Inject) AccountRepository vào đây.
        // 2. Lấy danh sách user và đưa vào model.

        return "admin/dashboard"; // Spring Boot sẽ tìm file dashboard.html trong thư mục templates
    }
}
