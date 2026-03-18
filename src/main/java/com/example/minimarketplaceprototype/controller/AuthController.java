package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.dto.UserRegistrationDto;
import com.example.minimarketplaceprototype.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    // Show the login page
    @GetMapping("/login")
    public String login() {
        return "login"; // This tells Spring to look for login.html
    }

    // Show the registration page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register"; // This tells Spring to look for register.html
    }

    // Handle the submission of the registration form
    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        userService.registerUser(registrationDto);
        return "redirect:/login?success"; // Send them back to log in with a success message
    }

    // Show the home page after successful login
    @GetMapping("/")
    public String home() {
        return "home"; // This tells Spring to look for home.html
    }
}