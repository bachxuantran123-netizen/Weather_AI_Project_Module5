package com.example.weather_ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MobileWebController {

    @GetMapping("/mobile-preview")
    public String showMobilePrototype() {
        return "mobile";
    }
}