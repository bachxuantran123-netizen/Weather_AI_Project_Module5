package com.example.weather_ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MobileWebController {
    @GetMapping("/")
    public String redirectToUser() {
        return "redirect:/mobile-preview";
    }

    @GetMapping("/mobile-preview")
    public String showMobilePrototype() {
        return "mobile";
    }

    @GetMapping("/oauth2-redirect")
    public String showOAuth2RedirectPage() {
        return "oauth2-redirect";
    }
}