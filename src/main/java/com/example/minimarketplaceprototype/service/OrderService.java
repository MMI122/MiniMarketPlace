package com.example.minimarketplaceprototype.service;

import com.example.minimarketplaceprototype.model.Order;
import java.util.List;

public interface OrderService {
    void placeOrder(Long productId, Integer quantity, String buyerUsername);
    List<Order> getOrdersByBuyer(String username);
    List<Order> getOrdersBySeller(String username);
}