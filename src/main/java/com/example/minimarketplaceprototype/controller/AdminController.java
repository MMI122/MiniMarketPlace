package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.service.ProductService;
import com.example.minimarketplaceprototype.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Fetch absolutely everything in the database
        model.addAttribute("users", userService.findAll());
        model.addAttribute("products", productService.getAllProducts());

        return "admin-dashboard"; // Looks for admin-dashboard.html
    }

    @PostMapping("/users/{id}/toggle-ban")
    public String toggleBan(@PathVariable("id") Long id) {
        userService.toggleBanStatus(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/message")
    public String sendMessage(@PathVariable("id") Long id, @RequestParam("message") String message) {
        userService.sendAdminMessage(id, message);
        return "redirect:/admin/dashboard?msgSuccess";
    }
}