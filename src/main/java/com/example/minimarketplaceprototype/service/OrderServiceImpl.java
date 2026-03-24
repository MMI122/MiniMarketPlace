package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.model.Order;
import com.example.minimarketplaceprototype.model.Product;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.OrderRepository;
import com.example.minimarketplaceprototype.repository.ProductRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void placeOrder(Long productId, Integer quantity, String buyerUsername) {
        // Find the buyer and the product
        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Create the new order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDateTime.now()); // Stamp the current date/time

        // Calculate total price: price * quantity
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        order.setTotalPrice(total);

        // Save to database
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByBuyer(String username) {
        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        return orderRepository.findByBuyerId(buyer.getId());
    }
}