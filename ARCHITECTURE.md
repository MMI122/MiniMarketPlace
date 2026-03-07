# 🏛️ Mini Marketplace — Architecture Document

## Overview

This document describes the technical architecture of the Mini Marketplace application. It serves as a reference for both team members during development and for the professor during evaluation.

---

## 1. Technology Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| Language | Java | 21 (LTS) | Core language |
| Framework | Spring Boot | 3.4.3 | Application framework |
| View Engine | Thymeleaf | (managed by Boot) | Server-side HTML rendering |
| Security | Spring Security | (managed by Boot) | Authentication & authorization |
| ORM | Spring Data JPA / Hibernate | (managed by Boot) | Database access |
| Database (prod) | PostgreSQL | 16 | Production & Docker database |
| Database (test) | H2 | (managed by Boot) | In-memory DB for tests |
| Build Tool | Maven | 3.9.x (via wrapper) | Build & dependency management |
| Containerization | Docker | Latest | Deployment containerization |
| CI/CD | GitHub Actions | N/A | Automated build, test, deploy |
| Hosting | Render | N/A | Cloud deployment |

---

## 2. Layered Architecture

The application follows a strict layered architecture. Each layer only communicates with the layer directly below it.

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Thymeleaf Templates (HTML)              │    │
│  │  login.html │ register.html │ products.html │ etc.  │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Controllers (@Controller)               │    │
│  │  AuthController │ ProductController │ OrderController│    │
│  │  AdminController                                     │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              DTOs (Data Transfer Objects)             │    │
│  │  UserRegistrationDto │ ProductDto │ OrderDto         │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│                      BUSINESS LAYER                          │
│  ┌─────────────────────────────────────────────────────┐    │
│  │          Service Interfaces + Implementations        │    │
│  │  UserService/Impl │ ProductService/Impl              │    │
│  │  OrderService/Impl │ CustomUserDetailsService        │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Design Pattern Components               │    │
│  │  Strategy: OrderPricingStrategy (pricing logic)      │    │
│  │  Factory: NotificationFactory (event notifications)  │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Exception Handling                       │    │
│  │  GlobalExceptionHandler (@ControllerAdvice)          │    │
│  │  ResourceNotFoundException │ DuplicateResourceEx.    │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│                     PERSISTENCE LAYER                        │
│  ┌─────────────────────────────────────────────────────┐    │
│  │         Repositories (Spring Data JPA)               │    │
│  │  UserRepo │ RoleRepo │ ProductRepo │ OrderRepo       │    │
│  └─────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                  JPA Entities                        │    │
│  │  User │ Role │ Product │ Order                       │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│                      DATA LAYER                              │
│  ┌─────────────────────────────────────────────────────┐    │
│  │     PostgreSQL (Docker container / Render DB)        │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│                    CROSS-CUTTING CONCERNS                     │
│  ┌──────────────────┐  ┌──────────────────────────────┐    │
│  │  Spring Security  │  │     Configuration             │    │
│  │  (Auth + RBAC)    │  │  SecurityConfig               │    │
│  │  BCrypt Encoder   │  │  Application Properties       │    │
│  └──────────────────┘  └──────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Entity Relationship Diagram

Exactly 4 tables. No join table. Each user has exactly ONE role.

```
┌───────────────┐                          ┌───────────────┐
│     Role      │                          │     User      │
│───────────────│                          │───────────────│
│ PK id: Long   │◄─── Many-to-One ────────│ PK id: Long   │
│    name: Enum │     (each user has       │    username   │
│  (ROLE_ADMIN, │      exactly one role)   │    password   │
│   ROLE_SELLER,│                          │ FK role_id    │
│   ROLE_BUYER) │                          └──┬─────┬──────┘
└───────────────┘                             │     │
                                 seller_id FK │     │ buyer_id FK
                                              │     │
                                         ┌────▼──┐  │
                                         │Product│  │
                                         │───────│  │
                                         │PK id  │  │
                                         │ name  │  │
                                         │ desc  │  │
                                         │ price │  │
                                         │created│  │
                                         └──┬────┘  │
                                            │       │
                                   product_id FK    │
                                            │       │
                                         ┌──▼───────▼──┐
                                         │    Order     │
                                         │──────────────│
                                         │ PK id        │
                                         │ quantity     │
                                         │ total_price  │
                                         │ order_date   │
                                         │ FK buyer_id  │
                                         │ FK product_id│
                                         └──────────────┘
```

### Relationship Summary
- **User → Role:** Many-to-One (each user has exactly one role)
- **User (seller) → Product:** One-to-Many
- **User (buyer) → Order:** One-to-Many
- **Product → Order:** One-to-Many

---

## 4. Security Architecture

```
                         HTTP Request
                              │
                              ▼
                  ┌───────────────────────┐
                  │   Spring Security     │
                  │   Filter Chain        │
                  │                       │
                  │ 1. UsernamePassword   │
                  │    AuthFilter         │
                  │ 2. Session Mgmt      │
                  │ 3. CSRF Protection   │
                  └───────┬───────────────┘
                          │
              ┌───────────┼───────────────┐
              │           │               │
         /admin/**   /seller/**     /buyer/**
         ADMIN only  SELLER only   BUYER only
              │           │               │
              ▼           ▼               ▼
         AdminCtrl   ProductCtrl    OrderCtrl
```

### Security Configuration Rules
```java
// URL-based security (in SecurityConfig)
/login, /register, /css/**, /          → permitAll()
/admin/**                              → hasRole("ADMIN")
/seller/**                             → hasRole("SELLER")
/buyer/**                              → hasRole("BUYER")
Everything else                        → authenticated()
```

### Password Flow
```
Registration:
  raw password → BCryptPasswordEncoder.encode() → stored in DB

Login:
  raw password → BCryptPasswordEncoder.matches() → compared with DB hash
```

---

## 5. Application Flow (Complete User Journey)

### Registration Flow
```
User → GET /register → Thymeleaf form
     → POST /register (username, password, role selection: SELLER or BUYER)
     → UserService.registerUser()
         → BCrypt encode password
         → Look up selected Role from DB
         → Save User with Many-to-One role link
     → Redirect to /login

Note: ADMIN is pre-seeded via DataInitializer on app startup.
      Users can only register as SELLER or BUYER.
```

### Seller Flow
```
Seller → Login → GET /seller/products (own products list)
       → GET /seller/products/new (create form)
       → POST /seller/products (save product, auto-link seller)
       → GET /seller/products/{id}/edit (edit form)
       → PUT /seller/products/{id} (update product)
       → DELETE /seller/products/{id} (remove product)
```

### Buyer Flow (Buy It Now)
```
Buyer → Login → GET /buyer/products (browse all products)
      → GET /buyer/products/{id} (view detail)
      → POST /buyer/orders/{productId} (instant buy)
          → OrderService.placeOrder()
              → Validate product exists
              → Validate buyer != seller
              → Calculate price via Strategy pattern
              → Create Order record
              → Trigger notification via Factory pattern
      → GET /buyer/orders (view own orders)
```

### Admin Flow
```
Admin → Login → GET /admin/dashboard (overview stats)
      → GET /admin/users (all users)
      → GET /admin/products (all products)
      → GET /admin/orders (all orders)
      → DELETE /admin/users/{id} (remove user)
```

---

## 6. Package Structure

```
com.example.minimarketplaceprototype
├── config/          → Security configuration, beans
├── controller/      → HTTP request handlers (return Thymeleaf views)
├── dto/             → Data Transfer Objects (form binding, view data)
├── enums/           → RoleName enum (ADMIN, SELLER, BUYER)
├── exception/       → Custom exceptions + Global handler
├── factory/         → Factory design pattern (notifications)
├── model/           → JPA entities (User, Role, Product, Order)
├── repository/      → Spring Data JPA repositories
├── service/         → Business logic (interfaces + implementations)
└── strategy/        → Strategy design pattern (pricing)
```

---

## 7. Environment Configuration Strategy

### Three Environments

| Environment | Database | Config Source | Purpose |
|---|---|---|---|
| **Test** | H2 in-memory | `application-test.properties` | Unit & integration tests |
| **Docker (local)** | PostgreSQL container | `docker-compose.yml` env vars | Local development/testing |
| **Render (prod)** | Render PostgreSQL | Render dashboard env vars | Production deployment |

### Properties Chain
```
application.properties          → base config + ${ENV_VAR} placeholders
application-test.properties     → H2 database for tests (overrides base)
docker-compose.yml              → provides env vars for Docker
Render dashboard                → provides env vars for production
```

---

## 8. CI/CD Pipeline Architecture

```
┌──────────┐     ┌──────────┐     ┌──────────────┐     ┌──────────┐
│ Developer │────►│  GitHub  │────►│GitHub Actions │────►│  Render  │
│ (push)    │     │ (repo)   │     │  (CI/CD)     │     │ (deploy) │
└──────────┘     └──────────┘     └──────────────┘     └──────────┘
                      │                    │
                      │              ┌─────┴──────┐
                      │              │  Pipeline  │
                      │              │            │
                      │              │ 1. Checkout│
                 feature/xxx         │ 2. JDK 21 │
                      │              │ 3. mvn     │
                      ▼              │    verify  │
                   develop           │ 4. Deploy  │
                      │              │    hook    │
                      ▼              └────────────┘
                    main
                 (protected)
```

### Pipeline Triggers
- **Push to `develop`:** Build + Test only
- **PR to `main`:** Build + Test (must pass to merge)
- **Push to `main` (after PR merge):** Build + Test + Deploy to Render

---

## 9. Docker Architecture

```
┌─────────────────────────────────────────────┐
│            docker-compose network            │
│                                             │
│  ┌─────────────────┐  ┌─────────────────┐  │
│  │   app (Spring)   │  │   db (Postgres)  │  │
│  │                  │  │                  │  │
│  │  Port: 8080      │  │  Port: 5432      │  │
│  │  Depends on: db  │──│  Volume: pgdata  │  │
│  │                  │  │                  │  │
│  │  ENV:            │  │  ENV:            │  │
│  │  DATASOURCE_URL  │  │  POSTGRES_DB     │  │
│  │  DATASOURCE_USER │  │  POSTGRES_USER   │  │
│  │  DATASOURCE_PASS │  │  POSTGRES_PASS   │  │
│  └─────────────────┘  └─────────────────┘  │
│                                             │
└─────────────────────────────────────────────┘
          │                      │
     localhost:8080         localhost:5432
     (app access)           (DB access, optional)
```

---

## 10. Design Pattern Integration

### Strategy Pattern (in OrderService)
```
┌──────────────────────┐
│ OrderPricingStrategy  │ ◄── Interface
│ + calculateTotal()    │
└───────┬──────┬────────┘
        │      │
┌───────▼──┐ ┌▼──────────────────┐
│ Regular  │ │    Discount        │
│ Pricing  │ │    Pricing         │
│ Strategy │ │    Strategy        │
└──────────┘ └────────────────────┘

OrderService uses Strategy to calculate order total.
Strategy can be swapped without modifying OrderService.
```

### Factory Pattern (in OrderService / ProductService)
```
┌──────────────────────┐
│  NotificationFactory  │
│  + create(type)       │
└───────────┬───────────┘
            │ creates
    ┌───────┴────────┐
    │                │
┌───▼────────┐ ┌────▼──────────┐
│   Email    │ │    System     │
│Notification│ │ Notification  │
└────────────┘ └───────────────┘

Factory decides which Notification to create based on event type.
Used after order placement and product listing events.
```

---

**Version:** 1.0 | **Last Updated:** March 7, 2026

