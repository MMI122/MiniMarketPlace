package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.dto.UserRegistrationDto;
import com.example.minimarketplaceprototype.model.Order;
import com.example.minimarketplaceprototype.service.OrderService;
import com.example.minimarketplaceprototype.service.ProductService;
import com.example.minimarketplaceprototype.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService; // NEW: Added ProductService!

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        userService.registerUser(registrationDto);
        return "redirect:/login?success";
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        // 1. ALWAYS fetch products so the homepage acts as a storefront
        model.addAttribute("products", productService.getAllProducts());

        // 2. Safely check if someone is actually logged in (Not a guest/anonymous)
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser");
        model.addAttribute("isLoggedIn", isLoggedIn);

        // 3. Only load the personalized dashboard stuff IF they are logged in
        if (isLoggedIn) {
            String username = authentication.getName();
            model.addAttribute("adminMessage", userService.findByUsername(username).getAdminMessage());

            // SELLER Revenue Logic
            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"))) {
                List<Order> sales = orderService.getOrdersBySeller(username);

                BigDecimal totalSales = sales.stream()
                        .map(Order::getTotalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalRevenue = totalSales.multiply(new BigDecimal("0.10"))
                        .setScale(2, java.math.RoundingMode.HALF_UP);

                model.addAttribute("sales", sales);
                model.addAttribute("totalSales", totalSales);
                model.addAttribute("totalRevenue", totalRevenue);
            }
        }
        return "home";
    }

    @PostMapping("/clear-message")
    public String clearMessage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            userService.clearAdminMessage(authentication.getName());
        }
        return "redirect:/";
    }
}