package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.dto.ProductDto;
import com.example.minimarketplaceprototype.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Show the marketplace to everyone
    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "products"; // This will look for products.html
    }

    // Show the "Add Product" form (We locked this down to SELLERs only in SecurityConfig!)
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new ProductDto());
        return "add-product"; // This will look for add-product.html
    }

    // Process the form submission when a Seller saves a new product
    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") ProductDto productDto, Authentication authentication) {
        // Get the username of the person logged in
        String username = authentication.getName();

        productService.addProduct(productDto, username);
        return "redirect:/products?success"; // Send them back to the marketplace with a success message
    }
}