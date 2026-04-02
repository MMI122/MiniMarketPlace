package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.model.Order;
import com.example.minimarketplaceprototype.model.Product;
import com.example.minimarketplaceprototype.model.User;
import com.example.minimarketplaceprototype.repository.OrderRepository;
import com.example.minimarketplaceprototype.repository.ProductRepository;
import com.example.minimarketplaceprototype.repository.UserRepository;
import com.example.minimarketplaceprototype.strategy.BulkDiscountPricingStrategy;
import com.example.minimarketplaceprototype.strategy.PricingStrategy;
import com.example.minimarketplaceprototype.strategy.RegularPricingStrategy;
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

        // 👇 THE STRATEGY PATTERN IN ACTION 👇
        PricingStrategy pricingStrategy;

        // If they buy 5 or more, dynamically switch to the Bulk Discount strategy!
        if (quantity >= 5) {
            pricingStrategy = new BulkDiscountPricingStrategy();
        } else {
            pricingStrategy = new RegularPricingStrategy();
        }

        // Convert BigDecimal to double for the strategy calculation
        double unitPrice = product.getPrice().doubleValue();

        // Execute the strategy
        double calculatedTotal = pricingStrategy.calculatePrice(unitPrice, quantity);

        // Convert the final double back to BigDecimal for safe database storage
        BigDecimal finalTotalPrice = BigDecimal.valueOf(calculatedTotal);
        // 👆 -------------------------------- 👆

        // Create the new order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setOrderDate(LocalDateTime.now()); // Stamp the current date/time
        order.setTotalPrice(finalTotalPrice); // Save the strategy-calculated price

        // Save to database
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByBuyer(String username) {
        User buyer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        return orderRepository.findByBuyerId(buyer.getId());
    }

    @Override
    public List<Order> getOrdersBySeller(String username) {
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        return orderRepository.findByProductSellerId(seller.getId());
    }
}