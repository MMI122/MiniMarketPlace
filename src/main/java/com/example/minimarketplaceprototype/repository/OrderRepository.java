package com.example.minimarketplaceprototype.repository;



import com.example.minimarketplaceprototype.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByBuyerId(Long buyerId);

}