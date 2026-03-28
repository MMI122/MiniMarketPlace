package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.model.Order;
import com.example.minimarketplaceprototype.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return ResponseEntity.status(401).build();
        }

        // Safely convert each Order into a clean Map
        List<Map<String, Object>> safeOrders = orderService.getOrdersByBuyer(authentication.getName())
                .stream()
                .map(this::mapToSafeJson)
                .collect(Collectors.toList());

        return ResponseEntity.ok(safeOrders);
    }

    // Helper method: Only grab the exact fields we want to show in the JSON
    private Map<String, Object> mapToSafeJson(Order o) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", o.getId());
        map.put("productName", o.getProduct().getName()); // Safely grab just the string name
        map.put("quantity", o.getQuantity());
        map.put("totalPrice", o.getTotalPrice());
        map.put("orderDate", o.getOrderDate());
        return map;
    }
}