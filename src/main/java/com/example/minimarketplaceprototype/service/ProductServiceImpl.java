package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.dto.ProductDto;
import com.example.minimarketplaceprototype.model.Order;
import com.example.minimarketplaceprototype.model.Product;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.OrderRepository;
import com.example.minimarketplaceprototype.repository.ProductRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository; // Injected OrderRepository!

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ==========================================
    // THE MISSING METHOD: Restored!
    // ==========================================
    @Override
    public List<Product> getProductsBySeller(String username) {
        return productRepository.findAll().stream()
                .filter(product -> product.getSeller().getUsername().equals(username))
                .collect(Collectors.toList());
    }

    @Override
    public void addProduct(ProductDto productDto, String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setSeller(seller);

        productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    @Transactional // Ensures that if one delete fails, the whole thing cancels safely
    public void deleteProduct(Long id, String username) {
        Product product = getProductById(id);

        // Security Check
        if (!product.getSeller().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: You do not own this product.");
        }

        // Find any orders that bought this product and delete them first
        List<Order> associatedOrders = orderRepository.findByProductId(id);
        orderRepository.deleteAll(associatedOrders);

        // Now that the orders are gone, it is safe to delete the product!
        productRepository.delete(product);
    }

    @Override
    public void updateProduct(Long id, ProductDto productDto, String username) {
        Product product = getProductById(id);
        if (!product.getSeller().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized: You do not own this product.");
        }
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        productRepository.save(product);
    }
}