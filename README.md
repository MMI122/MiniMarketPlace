# 👥 Project Contributions

Our `MiniMarketPlace` project was developed with a strict 50/50 division of labor to ensure both contributors demonstrated a mastery of Spring Boot, API design, architectural patterns, and automated testing. Below is the detailed breakdown of responsibilities and technical implementations.

---

# 👨‍💻 Contributor: Mehedi Hasan Rabby (GitHub: Mehedi-86)

## 📌 1. Overview of Responsibilities
My primary responsibilities encompassed the foundational entity relationships, the authentication layer, administrative controls, design pattern implementation, and the complete CI/CD cloud deployment architecture.

**Branches Owned & Merged:**
* `feature/product-order-entities`
* `feature/product-service-controller`
* `feature/auth-service-controller`
* `feature/factory-pattern`
* `feature/admin-dashboard`
* `Mehediunittest`
* `MehediIntegrationtest`

## 🏗️ 2. Core Feature Implementation
### Authentication & Security Layer
* **`ApiAuthController`:** Engineered the RESTful endpoints for secure user registration and login.
* **Role-Based Access Control (RBAC):** Integrated strict role validation (`ROLE_ADMIN`, `ROLE_BUYER`, `ROLE_SELLER`) during the registration pipeline to ensure users cannot escalate privileges maliciously.

### Administrative Capabilities
* **`Admin Dashboard`:** Implemented features strictly accessible to authenticated administrators.
* **Ban Mechanism:** Engineered the `toggleBanStatus` logic allowing admins to instantly revoke malicious user access.
* **Messaging System:** Built a targeted messaging system allowing admins to dispatch warnings directly to specific users via `sendAdminMessage` and `clearAdminMessage`.

### Product & Order Data Modeling
* **Entity Relationships:** Designed the JPA/Hibernate ORM mappings for `Product` and `Order` entities, ensuring proper `@ManyToOne` and `@OneToMany` relations with the `User` entity (e.g., Sellers owning Products, Buyers owning Orders).

## 🎨 3. Design Pattern: Factory Method
To handle the complex instantiation of different user types (Admin, Buyer, Seller), I implemented the **Factory Creational Design Pattern**.

**Why Factory?** Directly instantiating the `User` object scattered validation and encoding logic across the application. The `UserFactory` centralizes creation, automatically securely hashing passwords via `PasswordEncoder`, attaching the correct `Role` entity, and setting default system states (e.g., `isBanned = false`).

### 📊 UML Class Diagram: User Factory Pattern
```mermaid
classDiagram
    class UserRegistrationDto {
        -String username
        -String password
        -String role
    }
    
    class Role {
        -Long id
        -RoleName name
    }

    class User {
        -Long id
        -String username
        -String password
        -Role role
        -boolean isBanned
        -String adminMessage
    }

    class UserFactory {
        +createUser(UserRegistrationDto dto, Role role, String encodedPassword) User
    }

    class UserServiceImpl {
        -UserRepository userRepository
        -RoleRepository roleRepository
        -PasswordEncoder passwordEncoder
        -UserFactory userFactory
        +registerUser(UserRegistrationDto dto) void
    }

    UserServiceImpl --> UserFactory : Uses
    UserFactory ..> User : Instantiates
    UserRegistrationDto ..> UserFactory : Input
    Role ..> UserFactory : Input
```

### 🔁 UML Sequence Diagram: Registration Flow
```mermaid
sequenceDiagram
    actor Client
    participant ApiAuthController
    participant UserServiceImpl
    participant UserFactory
    participant UserRepository

    Client->>ApiAuthController: POST /api/auth/register (DTO)
    ApiAuthController->>UserServiceImpl: registerUser(dto)
    UserServiceImpl->>UserServiceImpl: validateRole()
    UserServiceImpl->>UserServiceImpl: encodePassword()
    UserServiceImpl->>UserFactory: createUser(dto, role, hash)
    UserFactory-->>UserServiceImpl: return User instance
    UserServiceImpl->>UserRepository: save(User)
    UserRepository-->>UserServiceImpl: Success
    UserServiceImpl-->>ApiAuthController: 201 Created
    ApiAuthController-->>Client: JSON Response
```

## 🧪 4. Testing & Quality Assurance
Adhering to strict Test-Driven standards, I authored exactly 50% of the project's testing suite, resulting in 100% CI pipeline success.

### Unit Testing (JUnit 5 & Mockito)
Authored **10 Unit Tests** focusing on the Service layer:
* **`UserServiceTest.java` (8 Tests):** Verified Factory Pattern instantiation, exception handling for missing roles, and Admin logic (banning/messaging). Used `@Mock` and `@InjectMocks` to simulate database interactions entirely in-memory.
* **`CustomUserDetailsServiceTest.java` (2 Tests):** Validated Spring Security's user loading mechanism and `UsernameNotFoundException` handling.

### Integration Testing (Spring Boot Test & MockMvc)
Authored **2 Integration Tests** utilizing an in-memory **H2 Database**:
* Configured `@ActiveProfiles("test")` and `@Transactional` to ensure a pristine database state per test.
* **Auth Controller Test:** Verified successful 201 HTTP status for user registration, actively bypassing Spring Security's CSRF protection using `.with(csrf())`.
* **Order Controller Test:** Simulated an authenticated session using `@WithMockUser` to test protected data retrieval.

## ☁️ 5. Cloud Architecture & Deployment
I was responsible for the transition from local development to production cloud infrastructure.
* **Database Provisioning:** Provisioned and secured a managed **PostgreSQL 16** instance on Render (Ohio, US East).
* **Web Service Deployment:** Deployed the Spring Boot application using Docker on Render.
* **Network Resolution:** Diagnosed and resolved cross-region network errors and JDBC URL formatting issues to successfully link the application layer to the database tier over the public internet using explicit credentials and environment variables.

---
---

# 👨‍💻 Contributor: Mubin (GitHub: MMI122)

## 📌 1. Overview of Responsibilities
My primary responsibilities for the MiniMarketPlace project centered around the core commerce engine, initial project architecture, the implementation of dynamic pricing algorithms, and ensuring robust public and private API access.

**Branches Owned & Merged:**
* `feature/phase0-project-setup`
* `feature/user-role-entities`
* `feature/security-config`
* `feature/api-implementation`
* `feature/order-service-controller`
* `feature/strategy-pattern`
* `Mubinunittest`
* `Mubinintegrationtest`

## 🏗️ 2. Core Feature Implementation
### Project Architecture & Setup
* **Phase 0 Configuration:** Initialized the Spring Boot application, configured the `pom.xml` dependencies, and set up the foundational folder and package structure.
* **Security Baseline:** Drafted the initial `SecurityConfig` to establish the stateless session management and base HTTP security filter chains.

### The Commerce Engine
* **Product Management (`ApiProductController`):** Built the complete CRUD lifecycle for the marketplace inventory, allowing sellers to safely add products and buyers to retrieve the global catalog.
* **Order Management (`ApiOrderController`):** Engineered the transactional logic for purchasing. This includes securely fetching user context from the active Spring Security session to explicitly tie new orders to the logged-in buyer entity.

## 🎨 3. Design Pattern: Strategy
To handle complex and scalable pricing logic, I implemented the **Strategy Behavioral Design Pattern**.

**Why Strategy?** Hardcoding pricing rules (like bulk discounts) into the `OrderService` using procedural `if/else` statements violates the Open/Closed Principle. By defining a `PricingStrategy` interface, the system can dynamically switch between a `RegularPricingStrategy` and a `BulkDiscountPricingStrategy` at runtime based on the quantity of items in the user's cart. This allows future sales, tax logic, or seasonal discounts to be added without modifying the core service.

### 📊 UML Class Diagram: Pricing Strategy Pattern
```mermaid
classDiagram
    class PricingStrategy {
        <<interface>>
        +calculatePrice(BigDecimal unitPrice, int quantity) BigDecimal
    }

    class RegularPricingStrategy {
        +calculatePrice(BigDecimal unitPrice, int quantity) BigDecimal
    }

    class BulkDiscountPricingStrategy {
        +calculatePrice(BigDecimal unitPrice, int quantity) BigDecimal
    }

    class OrderServiceImpl {
        -OrderRepository orderRepository
        -ProductRepository productRepository
        -UserRepository userRepository
        +placeOrder(Long productId, int quantity, String username) Order
    }

    PricingStrategy <|.. RegularPricingStrategy : Implements
    PricingStrategy <|.. BulkDiscountPricingStrategy : Implements
    OrderServiceImpl --> PricingStrategy : Uses
```

### 🔁 UML Sequence Diagram: Order Placement Flow
```mermaid
sequenceDiagram
    actor Buyer
    participant ApiOrderController
    participant OrderServiceImpl
    participant PricingStrategy
    participant OrderRepository

    Buyer->>ApiOrderController: POST /api/orders?productId=1&qty=5
    ApiOrderController->>OrderServiceImpl: placeOrder(1, 5, "buyer")
    OrderServiceImpl->>OrderServiceImpl: determineStrategy(5)
    Note right of OrderServiceImpl: Quantity >= 5 triggers Bulk Strategy
    OrderServiceImpl->>PricingStrategy: calculatePrice(1000.00, 5)
    PricingStrategy-->>OrderServiceImpl: return 4500.00 (10% off)
    OrderServiceImpl->>OrderRepository: save(Order)
    OrderRepository-->>OrderServiceImpl: Success
    OrderServiceImpl-->>ApiOrderController: Order Entity
    ApiOrderController-->>Buyer: 200 OK (JSON Response)
```

## 🧪 4. Testing & Quality Assurance
I authored the remaining 50% of the automated testing suite, ensuring the commerce logic and endpoint security rules were completely bulletproof.

### Unit Testing (JUnit 5 & Mockito)
Authored **10 Unit Tests** focusing on the Commerce layer:
* **`OrderServiceTest.java` (5 Tests):** Heavily tested the Strategy Pattern to ensure orders below the threshold received regular pricing, while bulk orders accurately applied the 10% discount. Validated that attempting to place an order for a non-existent user threw the correct `RuntimeException`.
* **`ProductServiceTest.java` (5 Tests):** Verified the successful creation, retrieval, and deletion of products using mocked repository layers.

### Integration Testing (Spring Boot Test & MockMvc)
Authored **2 Integration Tests** utilizing an in-memory **H2 Database**:
* Configured to run cleanly with `@Transactional` and `@ActiveProfiles("test")`.
* **Public Endpoint Test:** Verified that `GET /api/products` successfully returns a 200 OK and a JSON array without requiring any authentication tokens.
* **Security Redirect Test:** Intentionally fired a request to a protected endpoint (`/api/orders/my-orders`) without an authenticated session. Successfully asserted that Spring Security intercepts the request and issues a `302 Redirection` to the `/login` page, proving the security filter chain restricts unauthorized access as intended.