package com.example.weather_ai.controller.admin;

import com.example.weather_ai.dto.CreateUserDTO;
import com.example.weather_ai.dto.UserDTO;
import com.example.weather_ai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model,
                        @CookieValue(value = "phoneNumber", defaultValue = "") String phoneNumber) {
        UserDTO login = new UserDTO();
        login.setPhoneNumber(phoneNumber);
        model.addAttribute("loginRequest", login);
        return "admin/auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        CreateUserDTO register = new CreateUserDTO();
        model.addAttribute("register", register);
        return "admin/auth/register";
    }

    @PostMapping("/register")
    public String store(@Valid @ModelAttribute("register") CreateUserDTO createUserDTO,
                        BindingResult bindingResult,
                        Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/auth/register";
        }

        try {
            userService.registerNewUser(createUserDTO);
        } catch (Exception e) {
            model.addAttribute("error", "Số điện thoại hoặc Email đã tồn tại!");
            return "admin/auth/register";
        }

        return "redirect:/auth/login?success=true";
    }
}