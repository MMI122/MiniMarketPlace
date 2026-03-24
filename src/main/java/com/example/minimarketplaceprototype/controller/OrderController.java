package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Process the "Buy" button click
    @PostMapping("/buy")
    public String buyProduct(@RequestParam("productId") Long productId,
                             @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                             Authentication authentication) {
        String username = authentication.getName();
        orderService.placeOrder(productId, quantity, username);

        return "redirect:/orders?success"; // Send them to their order history
    }

    // Show the buyer their personal order history
    @GetMapping
    public String listMyOrders(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("orders", orderService.getOrdersByBuyer(username));

        return "orders"; // This tells Spring to look for orders.html
    }
}