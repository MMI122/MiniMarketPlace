package com.example.minimarketplaceprototype.controller;

import com.example.minimarketplaceprototype.dto.ProductDto;
import com.example.minimarketplaceprototype.model.Product;
import com.example.minimarketplaceprototype.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    // ==========================================
    // NEW METHODS: UPDATE & DELETE
    // ==========================================

    // Show the Edit Form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, Authentication authentication) {
        Product product = productService.getProductById(id);

        // Security Check: If they don't own it, kick them back to the products page
        if (!product.getSeller().getUsername().equals(authentication.getName())) {
            return "redirect:/products?error=unauthorized";
        }

        // Prepare the DTO to pre-fill the form
        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());

        model.addAttribute("product", dto);
        model.addAttribute("productId", id); // We pass the ID so the HTML knows it's an update!

        return "add-product"; // Reusing the same HTML file!
    }

    // Process the Edit
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute("product") ProductDto productDto, Authentication authentication) {
        productService.updateProduct(id, productDto, authentication.getName());
        return "redirect:/products?success=updated";
    }

    // Process the Delete
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, Authentication authentication) {
        productService.deleteProduct(id, authentication.getName());
        return "redirect:/products?success=deleted";
    }
}