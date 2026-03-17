package com.example.minimarketplaceprototype.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDto {
    private Long id;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime orderDate;
    private Long productId;
    private String productName;
}