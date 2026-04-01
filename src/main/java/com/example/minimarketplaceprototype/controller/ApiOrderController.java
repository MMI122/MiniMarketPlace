package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.model.Order;
import com.example.minimarketplaceprototype.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ApiOrderController {

    private final OrderService orderService;

    @GetMapping("/my-orders")
    public ResponseEntity<List<Map<String, Object>>> getMyOrdersApi(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Map<String, Object>> safeOrders = orderService.getOrdersByBuyer(authentication.getName())
                .stream()
                .map(this::mapToSafeJson)
                .collect(Collectors.toList());
        return ResponseEntity.ok(safeOrders);
    }

    @PostMapping("/buy")
    public ResponseEntity<Map<String, String>> buyProductApi(@RequestParam Long productId, @RequestParam int quantity, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        orderService.placeOrder(productId, quantity, authentication.getName());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully purchased product ID: " + productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private Map<String, Object> mapToSafeJson(Order o) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", o.getId());
        map.put("productName", o.getProduct().getName());
        map.put("quantity", o.getQuantity());
        map.put("totalPrice", o.getTotalPrice());
        map.put("orderDate", o.getOrderDate());
        return map;
    }
}