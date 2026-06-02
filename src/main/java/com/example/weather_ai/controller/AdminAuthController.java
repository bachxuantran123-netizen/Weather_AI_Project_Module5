package com.example.weather_ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminAuthController {

    @GetMapping("/admin-login")
    public String showLoginPage() {
        return "admin/login";
    }
}