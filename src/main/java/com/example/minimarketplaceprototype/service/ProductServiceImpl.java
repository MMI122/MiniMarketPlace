package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.dto.ProductDto;
import com.example.minimarketplaceprototype.model.Product;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.ProductRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void addProduct(ProductDto productDto, String sellerUsername) {
        // Find the specific user who is currently logged in
        User seller = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // Create and populate the new Product entity
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setSeller(seller);

        // Save it to the database
        productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsBySeller(String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return productRepository.findBySellerId(seller.getId());
    }
}