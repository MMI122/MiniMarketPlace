# Design Pattern 1 — Strategy Pattern

**Author:** Mubin  
**Pattern:** Strategy (Behavioral Design Pattern)  
**Location:** `com.example.minimarketplaceprototype.strategy` package

---

## 1. What is the Strategy Pattern?

The Strategy Pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable. It lets the algorithm vary independently from the clients that use it.

Instead of hardcoding a single way to calculate something, we define a **common interface** and create **multiple implementations**. The client (our service) receives the strategy at runtime and delegates the work to it.

---

## 2. Why We Chose It

In our Mini Marketplace, orders need a **price calculation**. Rather than writing a single `if/else` block inside `OrderService`, we created a Strategy interface so that:

- Different pricing rules can be swapped without modifying `OrderService`
- Adding a new pricing rule (e.g., "Holiday Discount") requires only a new class — **zero changes to existing code**
- This follows the **Open/Closed Principle** (open for extension, closed for modification)

---

## 3. Class Diagram (Text-Based)

```
         ┌──────────────────────────────┐
         │  <<interface>>               │
         │  OrderPricingStrategy        │
         │──────────────────────────────│
         │ + calculateTotal(            │
         │     price: BigDecimal,       │
         │     quantity: int            │
         │   ): BigDecimal              │
         └──────────┬─────────┬─────────┘
                    │         │
          implements│         │implements
                    │         │
   ┌────────────────▼──┐  ┌──▼──────────────────┐
   │ RegularPricing    │  │ DiscountPricing      │
   │ Strategy          │  │ Strategy             │
   │───────────────────│  │──────────────────────│
   │ @Component        │  │ @Component           │
   │ calculateTotal()  │  │ discountPercent: int  │
   │ → price * qty     │  │ calculateTotal()     │
   └───────────────────┘  │ → price*qty*(1-disc) │
                          └──────────────────────┘
                          
   ┌──────────────────────────────┐
   │      OrderServiceImpl        │
   │──────────────────────────────│
   │ - pricingStrategy:           │
   │     OrderPricingStrategy     │
   │──────────────────────────────│
   │ + placeOrder(...)            │
   │   → uses pricingStrategy     │
   │     .calculateTotal()        │
   └──────────────────────────────┘
```

---

## 4. Where It Is Implemented (File Paths)

| File | Path | Purpose |
|---|---|---|
| `OrderPricingStrategy.java` | `src/main/java/.../strategy/OrderPricingStrategy.java` | Interface defining the contract |
| `RegularPricingStrategy.java` | `src/main/java/.../strategy/RegularPricingStrategy.java` | Default pricing: `price × quantity` |
| `DiscountPricingStrategy.java` | `src/main/java/.../strategy/DiscountPricingStrategy.java` | Discounted pricing: `price × quantity × (1 - discount%)` |
| `OrderServiceImpl.java` | `src/main/java/.../service/OrderServiceImpl.java` | Client that uses the strategy |

---

## 5. Code Examples

### Interface
```java
public interface OrderPricingStrategy {
    BigDecimal calculateTotal(BigDecimal price, int quantity);
}
```

### Regular Pricing (Default)
```java
@Component
public class RegularPricingStrategy implements OrderPricingStrategy {
    @Override
    public BigDecimal calculateTotal(BigDecimal price, int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
```

### Discount Pricing
```java
@Component
public class DiscountPricingStrategy implements OrderPricingStrategy {
    private static final BigDecimal DISCOUNT = new BigDecimal("0.10"); // 10% off

    @Override
    public BigDecimal calculateTotal(BigDecimal price, int quantity) {
        BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discountAmount = total.multiply(DISCOUNT);
        return total.subtract(discountAmount);
    }
}
```

### Usage in OrderService
```java
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderPricingStrategy pricingStrategy;
    // ... other dependencies

    public OrderServiceImpl(
            @Qualifier("regularPricingStrategy") OrderPricingStrategy pricingStrategy,
            // ... other params
    ) {
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public Order placeOrder(Long productId, User buyer, int quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        BigDecimal total = pricingStrategy.calculateTotal(product.getPrice(), quantity);

        Order order = new Order();
        order.setBuyer(buyer);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setTotalPrice(total);
        order.setOrderDate(LocalDateTime.now());

        return orderRepository.save(order);
    }
}
```

---

## 6. Benefits of Using This Pattern

1. **Open/Closed Principle:** Adding a new pricing rule (e.g., `BulkPricingStrategy`) requires only a new class. No existing code changes.
2. **Single Responsibility:** Each strategy class has one job — calculate price a specific way.
3. **Testability:** Each strategy can be unit tested independently. The `OrderService` can be tested with mock strategies.
4. **Runtime Flexibility:** The strategy can be swapped via Spring's `@Qualifier` or even dynamically at runtime.

---

## 7. Alternative Approaches We Considered

| Approach | Why We Didn't Use It |
|---|---|
| `if/else` in OrderService | Violates Open/Closed. Adding new rules means modifying existing code. |
| Enum with methods | Less flexible, harder to inject dependencies into enum instances. |
| Inheritance (subclass OrderService) | Creates tight coupling, harder to test, less flexible. |

The Strategy Pattern was the cleanest solution for our use case.

---

**Pattern Category:** Behavioral  
**SOLID Principle Applied:** Open/Closed Principle, Single Responsibility Principle  
**Author:** Mubin

