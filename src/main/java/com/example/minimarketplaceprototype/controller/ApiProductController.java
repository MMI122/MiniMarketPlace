package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.model.Product;
import com.example.minimarketplaceprototype.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ApiProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllProductsApi() {
        // Safely convert each Product into a clean Map to avoid Hibernate Proxy crashes
        List<Map<String, Object>> safeProducts = productService.getAllProducts().stream()
                .map(this::mapToSafeJson)
                .collect(Collectors.toList());
        return ResponseEntity.ok(safeProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductByIdApi(@PathVariable Long id) {
        return ResponseEntity.ok(mapToSafeJson(productService.getProductById(id)));
    }

    // Helper method: Only grab the exact fields we want to show in the JSON
    private Map<String, Object> mapToSafeJson(Product p) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("name", p.getName());
        map.put("price", p.getPrice());
        map.put("description", p.getDescription());
        map.put("sellerName", p.getSeller().getUsername()); // Safely grab just the string name
        return map;
    }
}