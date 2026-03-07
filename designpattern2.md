# Design Pattern 2 — Factory Pattern

**Author:** Teammate  
**Pattern:** Factory (Creational Design Pattern)  
**Location:** `com.example.minimarketplaceprototype.factory` package

---

## 1. What is the Factory Pattern?

The Factory Pattern provides an interface for creating objects without specifying their exact classes. A factory method decides which concrete class to instantiate based on input parameters.

Instead of using `new` directly in business logic, we delegate object creation to a Factory class. This decouples the creation logic from the business logic.

---

## 2. Why We Chose It

In our Mini Marketplace, different events trigger different types of notifications:
- When a **buyer places an order** → System notification (logged internally)
- When a **seller lists a product** → Email notification (to admin)

Rather than using `if/else` blocks with `new EmailNotification()` or `new SystemNotification()` scattered in our services, we centralize creation in a `NotificationFactory`. This way:

- Adding a new notification type (e.g., `SmsNotification`) requires only a new class + one line in the factory
- Services don't need to know which concrete Notification class exists
- Follows the **Single Responsibility Principle**: factory creates, services use

---

## 3. Class Diagram (Text-Based)

```
         ┌──────────────────────────────┐
         │  <<interface>>               │
         │  Notification                │
         │──────────────────────────────│
         │ + send(message: String):void │
         │ + getType(): String          │
         └──────────┬─────────┬─────────┘
                    │         │
          implements│         │implements
                    │         │
   ┌────────────────▼──┐  ┌──▼──────────────────┐
   │ EmailNotification │  │ SystemNotification   │
   │───────────────────│  │──────────────────────│
   │ send(msg)         │  │ send(msg)            │
   │ → logs email sim. │  │ → logs system event  │
   │ getType()="EMAIL" │  │ getType()="SYSTEM"   │
   └───────────────────┘  └──────────────────────┘

   ┌──────────────────────────────────────┐
   │       NotificationFactory            │
   │──────────────────────────────────────│
   │ + createNotification(               │
   │     type: String                    │
   │   ): Notification                    │
   │                                      │
   │ "ORDER_PLACED" → SystemNotification  │
   │ "PRODUCT_LISTED" → EmailNotification │
   └──────────────────────────────────────┘

   ┌──────────────────────────────┐
   │      OrderServiceImpl        │
   │──────────────────────────────│
   │ - notificationFactory:       │
   │     NotificationFactory      │
   │──────────────────────────────│
   │ + placeOrder(...)            │
   │   → factory.createNotif()   │
   │   → notification.send()     │
   └──────────────────────────────┘
```

---

## 4. Where It Is Implemented (File Paths)

| File | Path | Purpose |
|---|---|---|
| `Notification.java` | `src/main/java/.../factory/Notification.java` | Interface defining notification contract |
| `EmailNotification.java` | `src/main/java/.../factory/EmailNotification.java` | Email notification implementation (simulated via logging) |
| `SystemNotification.java` | `src/main/java/.../factory/SystemNotification.java` | System/internal notification implementation |
| `NotificationFactory.java` | `src/main/java/.../factory/NotificationFactory.java` | Factory class that creates appropriate notification |
| `OrderServiceImpl.java` | `src/main/java/.../service/OrderServiceImpl.java` | Client that uses the factory after order placement |

---

## 5. Code Examples

### Interface
```java
public interface Notification {
    void send(String message);
    String getType();
}
```

### Email Notification
```java
public class EmailNotification implements Notification {

    private static final Logger log = LoggerFactory.getLogger(EmailNotification.class);

    @Override
    public void send(String message) {
        log.info("[EMAIL NOTIFICATION] {}", message);
        // In production, this would integrate with an email service
    }

    @Override
    public String getType() {
        return "EMAIL";
    }
}
```

### System Notification
```java
public class SystemNotification implements Notification {

    private static final Logger log = LoggerFactory.getLogger(SystemNotification.class);

    @Override
    public void send(String message) {
        log.info("[SYSTEM NOTIFICATION] {}", message);
    }

    @Override
    public String getType() {
        return "SYSTEM";
    }
}
```

### Factory
```java
@Component
public class NotificationFactory {

    public Notification createNotification(String eventType) {
        return switch (eventType) {
            case "ORDER_PLACED" -> new SystemNotification();
            case "PRODUCT_LISTED" -> new EmailNotification();
            default -> throw new IllegalArgumentException(
                "Unknown event type: " + eventType
            );
        };
    }
}
```

### Usage in OrderService
```java
@Service
public class OrderServiceImpl implements OrderService {

    private final NotificationFactory notificationFactory;
    // ... other dependencies

    @Override
    public Order placeOrder(Long productId, User buyer, int quantity) {
        // ... create order logic ...

        Order savedOrder = orderRepository.save(order);

        // Factory pattern: create and send notification
        Notification notification = notificationFactory.createNotification("ORDER_PLACED");
        notification.send("Order #" + savedOrder.getId() + " placed by " + buyer.getUsername());

        return savedOrder;
    }
}
```

---

## 6. Benefits of Using This Pattern

1. **Decoupled Creation:** Services don't know about concrete notification classes. They only depend on the `Notification` interface and `NotificationFactory`.
2. **Single Responsibility:** Factory handles creation logic. Notification classes handle sending logic. Services handle business logic.
3. **Easy Extension:** Adding `SmsNotification` requires:
   - One new class implementing `Notification`
   - One new case in the factory's switch
   - Zero changes to any service
4. **Testability:** Factory can be mocked in unit tests. Each notification type can be tested independently.

---

## 7. Alternative Approaches We Considered

| Approach | Why We Didn't Use It |
|---|---|
| Direct `new` in service | Tight coupling, service knows about all notification types. Violates Single Responsibility. |
| Abstract Factory | Overkill for 2 notification types. Simple Factory is sufficient here. |
| Observer Pattern | Could work for events, but Factory is simpler and more explicit for our needs. We reserve Observer for other use cases. |

The Factory Pattern was the best fit for centralizing notification creation while keeping our services clean.

---

**Pattern Category:** Creational  
**SOLID Principle Applied:** Single Responsibility Principle, Open/Closed Principle  
**Author:** Teammate

