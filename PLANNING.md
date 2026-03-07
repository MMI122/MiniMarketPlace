# 🏗️ Mini Marketplace — Complete Project Plan

**Team:** Mubin (Windows) + Teammate (MacBook)  
**Repo:** https://github.com/MMI122/MiniMarketPlace.git  
**Theme:** Mini Marketplace — "Buy It Now" model  
**Date:** March 7, 2026

---

## 📋 Executive Summary

We build a **minimal but complete** Mini Marketplace to demonstrate a professional software development workflow. The strategy is **zero feature bloat** — exactly 4 tables, 3 roles, instant buy (no cart), and maximum time for Docker, CI/CD, and Testing (worth 40 marks combined).

### Why Mini Marketplace Wins
- **Perfect role mapping:** Admin / Seller / Buyer — native fit, zero confusion for grader
- **Pure CRUD:** Buyer clicks buy → one row in Order table. Easiest to test
- **Maximum DevOps time:** Simple backend = more time for Docker, CI/CD, Render (automatic failure if missing)

---

## 🗄️ Database Design (Exactly 4 Tables — No More, No Less)

Each user has exactly ONE role. A buyer clicks "Buy" and one row is created in the Order table. No shopping cart. No order items table.

```
┌──────────────┐
│    roles     │
│──────────────│
│ id (PK)      │
│ name (enum)  │─────────────────────┐
│  ROLE_ADMIN  │                     │
│  ROLE_SELLER │                     │ Many-to-One
│  ROLE_BUYER  │                     │ (each user has one role)
└──────────────┘                     │
                                     │
┌──────────────────────────────┐     │
│          users               │     │
│──────────────────────────────│     │
│ id (PK)                      │     │
│ username (unique)            │     │
│ password (BCrypt hashed)     │     │
│ role_id (FK) ────────────────┘     │
└──────┬───────────┬───────────┘
       │           │
       │ (seller)  │ (buyer)
       ▼           ▼
┌──────────────┐  ┌──────────────────┐
│  products    │  │     orders       │
│──────────────│  │──────────────────│
│ id (PK)      │  │ id (PK)          │
│ name         │  │ quantity         │
│ description  │  │ total_price      │
│ price        │  │ order_date       │
│ created_at   │  │ buyer_id (FK)    │
│ seller_id(FK)│  │ product_id (FK)  │
└──────┬───────┘  └──────────────────┘
       │                    ▲
       └────────────────────┘
         (one product → many orders)
```

### Relationships
| Relationship | Type | Description |
|---|---|---|
| User → Role | Many-to-One | Each user has exactly ONE role (ADMIN, SELLER, or BUYER) |
| User → Product | One-to-Many | A seller owns many products |
| User → Order | One-to-Many | A buyer has many orders |
| Product → Order | One-to-Many | A product can be in many orders |

---

## 🔐 Role-Based Access Control (ALL 3 ROLES ARE MANDATORY)

The system **MUST** have exactly these 3 roles. They are not optional — missing any role = automatic failure.

| Role | Can Access | Can Do |
|---|---|---|
| **ADMIN** | `/admin/**` | View all users, products, orders. Delete users. Read-only monitoring. Cannot buy or sell. |
| **SELLER** | `/seller/**` | CRUD own products. View own product list. Cannot buy. |
| **BUYER** | `/buyer/**` | Browse all products. Place instant orders. View own orders. Cannot sell. |
| **Anyone** | `/`, `/login`, `/register` | Public pages, registration, login. |

### Registration Flow
- During registration, user selects role: **SELLER** or **BUYER**
- **ADMIN** is pre-seeded via `DataInitializer` (not selectable during registration)
- Each user has exactly ONE role (Many-to-One relationship)

---

## 🌐 URL & Controller Design (Thymeleaf Views, Not JSON)

> **Teacher instruction interpretation:** Controllers return **Thymeleaf HTML views** using REST-style URL design (proper HTTP methods: GET, POST, PUT, DELETE). No JSON responses needed. We use Thymeleaf's hidden `_method` field for PUT/DELETE.

### AuthController
| Method | URL | Action | View |
|---|---|---|---|
| GET | `/login` | Show login form | `login.html` |
| GET | `/register` | Show registration form | `register.html` |
| POST | `/register` | Process registration → redirect to login | redirect |

### ProductController (Seller Endpoints)
| Method | URL | Action | View |
|---|---|---|---|
| GET | `/seller/products` | List seller's own products | `seller/products.html` |
| GET | `/seller/products/new` | Show create product form | `seller/product-form.html` |
| POST | `/seller/products` | Create new product | redirect |
| GET | `/seller/products/{id}/edit` | Show edit form | `seller/product-form.html` |
| PUT | `/seller/products/{id}` | Update product | redirect |
| DELETE | `/seller/products/{id}` | Delete product | redirect |

### OrderController (Buyer Endpoints)
| Method | URL | Action | View |
|---|---|---|---|
| GET | `/buyer/products` | Browse all available products | `buyer/products.html` |
| GET | `/buyer/products/{id}` | View product detail | `buyer/product-detail.html` |
| POST | `/buyer/orders/{productId}` | Place order (Buy It Now) | redirect |
| GET | `/buyer/orders` | View own orders | `buyer/orders.html` |

### AdminController
| Method | URL | Action | View |
|---|---|---|---|
| GET | `/admin/dashboard` | Admin dashboard with stats | `admin/dashboard.html` |
| GET | `/admin/users` | List all users | `admin/users.html` |
| GET | `/admin/products` | List all products | `admin/products.html` |
| GET | `/admin/orders` | List all orders | `admin/orders.html` |
| DELETE | `/admin/users/{id}` | Delete a user | redirect |

**Total: 4 Controllers (satisfies minimum 3) with CRUD on Product and Order entities (satisfies minimum 2).**

---

## 🏛️ Layered Architecture

```
┌─────────────────────────────────────────────────┐
│                  Thymeleaf Views                │
│           (HTML templates + Bootstrap)           │
├─────────────────────────────────────────────────┤
│                  Controllers                     │
│   AuthController │ ProductController │ etc.      │
├─────────────────────────────────────────────────┤
│                  DTOs                            │
│   UserRegistrationDto │ ProductDto │ OrderDto    │
├─────────────────────────────────────────────────┤
│                 Service Layer                    │
│   UserService │ ProductService │ OrderService    │
│         (Business logic + validation)            │
├─────────────────────────────────────────────────┤
│                Repository Layer                  │
│   UserRepo │ ProductRepo │ OrderRepo │ RoleRepo  │
├─────────────────────────────────────────────────┤
│             JPA / Hibernate                      │
├─────────────────────────────────────────────────┤
│               PostgreSQL                         │
└─────────────────────────────────────────────────┘
```

---

## 🎨 Design Patterns (2 Required)

### Pattern 1 — Strategy Pattern (Mubin)
**Where:** Order pricing calculation in `OrderService`  
**What:** `OrderPricingStrategy` interface with `RegularPricingStrategy` and `DiscountPricingStrategy` implementations  
**Why:** Allows swapping pricing logic without modifying service code. Clean Open/Closed Principle.  
**Documentation:** `designpattern1.md`

### Pattern 2 — Factory Pattern (Teammate)
**Where:** Notification creation after events (order placed, product listed)  
**What:** `NotificationFactory` returns `EmailNotification` or `SystemNotification` based on event type  
**Why:** Decouples notification creation from business logic. Easy to extend with new notification types.  
**Documentation:** `designpattern2.md`

---

## 🧪 Test Strategy

### Unit Tests (15+ minimum, Service layer)

| Test Class | Test Method | What It Tests | Author |
|---|---|---|---|
| `UserServiceTest` | `registerUser_success` | Happy path registration | Mubin |
| `UserServiceTest` | `registerUser_duplicateUsername_throws` | Duplicate username rejection | Teammate |
| `UserServiceTest` | `findByUsername_found` | User lookup | Mubin |
| `UserServiceTest` | `findByUsername_notFound_throws` | Missing user exception | Teammate |
| `ProductServiceTest` | `createProduct_success` | Happy path product creation | Teammate |
| `ProductServiceTest` | `getAllProducts_returnsList` | Product listing | Mubin |
| `ProductServiceTest` | `getProductById_found` | Product lookup | Teammate |
| `ProductServiceTest` | `getProductById_notFound_throws` | Missing product exception | Mubin |
| `ProductServiceTest` | `updateProduct_success` | Product update | Mubin |
| `ProductServiceTest` | `deleteProduct_success` | Product deletion | Teammate |
| `ProductServiceTest` | `getProductsBySeller_returnsList` | Seller's products | Mubin |
| `OrderServiceTest` | `placeOrder_success` | Happy path order creation | Teammate |
| `OrderServiceTest` | `placeOrder_productNotFound_throws` | Invalid product order | Mubin |
| `OrderServiceTest` | `getOrdersByBuyer_returnsList` | Buyer's orders | Teammate |
| `OrderServiceTest` | `calculateTotal_regularPricing` | Strategy pattern — regular | Mubin |
| `OrderServiceTest` | `calculateTotal_discountPricing` | Strategy pattern — discount | Mubin |
| `OrderServiceTest` | `placeOrder_selfPurchase_throws` | Seller can't buy own product | Teammate |

**Total: 17 unit tests (9 Mubin + 8 Teammate)** ✅

### Integration Tests (3+ minimum, Controller layer)

| Test Class | Test Method | What It Tests | Author |
|---|---|---|---|
| `AuthControllerIntegrationTest` | `registerPage_returns200` | Registration page loads | Teammate |
| `AuthControllerIntegrationTest` | `registerUser_redirectsToLogin` | Registration flow works | Teammate |
| `ProductControllerIntegrationTest` | `productList_authenticatedSeller_returns200` | Seller can see products | Mubin |
| `ProductControllerIntegrationTest` | `productList_unauthenticated_redirectsToLogin` | Security blocks anonymous | Mubin |
| `AdminControllerIntegrationTest` | `adminDashboard_withBuyerRole_returns403` | Role enforcement works | Teammate |

**Total: 5 integration tests (2 Mubin + 3 Teammate)** ✅

### Test Infrastructure
- **Unit tests:** `@ExtendWith(MockitoExtension.class)`, `@Mock` repositories, `@InjectMocks` services
- **Integration tests:** `@SpringBootTest`, `@AutoConfigureMockMvc`, `@ActiveProfiles("test")`, H2 in-memory DB
- **Test profile:** `src/test/resources/application-test.properties` (H2 database, no Docker needed in CI)

---

## 🐳 Docker Strategy

### Dockerfile (Multi-stage build)
```
Stage 1: maven:3.9-eclipse-temurin-21 → mvn clean package -DskipTests
Stage 2: eclipse-temurin:21-jre → copy jar → run
```

### docker-compose.yml
```
services:
  db:      PostgreSQL 16, env vars, health check, volume
  app:     Build from Dockerfile, depends_on db, env vars, port 8080
```

### Environment Variables (never hardcoded)
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `.env.example` committed (template), `.env` in `.gitignore`

---

## 🔄 Git Workflow & CI/CD

### Branch Strategy
```
main (protected — PR + 1 approval + CI pass required)
  └── develop
        ├── feature/user-role-entities        (Mubin)
        ├── feature/product-order-entities     (Teammate)
        ├── feature/security-config            (Mubin)
        ├── feature/exception-handling         (Teammate)
        ├── feature/product-service-controller (Mubin)
        ├── feature/auth-service-controller    (Teammate)
        ├── feature/order-service-controller   (Teammate)
        ├── feature/admin-controller           (Mubin)
        ├── feature/strategy-pattern           (Mubin)
        ├── feature/factory-pattern            (Teammate)
        ├── feature/unit-tests-mubin           (Mubin)
        ├── feature/unit-tests-teammate        (Teammate)
        ├── feature/integration-tests-mubin    (Mubin)
        ├── feature/integration-tests-teammate (Teammate)
        ├── feature/docker-setup               (Mubin)
        ├── feature/cicd-deploy                (Mubin)
        ├── feature/render-setup               (Mubin)
        └── feature/readme                     (Both)
```

### CI/CD Pipeline (GitHub Actions)
```
Push to develop or PR to main
  → Checkout code
  → Setup JDK 21 (Temurin)
  → ./mvnw clean verify (compile + run all tests)
  → ✅ Pass → PR can be merged

Push to main (after PR merge)
  → Same build + test
  → Deploy to Render (webhook trigger)
```

### Bootstrap Sequence (IMPORTANT — Do This First)
1. Mubin pushes skeleton + `ci.yml` + config files directly to `main` (ONE TIME ONLY)
   - This single push includes: fixed pom.xml, .gitattributes, ci.yml, Dockerfile, compose.yaml, application.properties, etc.
   - Yes, ci.yml goes with this push — that's the whole point
2. Create `develop` branch from `main`
   - **Why `develop`?** Because once `main` is protected, you can't merge feature branches into it without CI passing. `develop` is where features are integrated and tested together. Only stable `develop` gets promoted to `main` via PR.
3. THEN enable branch protection on `main`
4. All future work: `feature → develop` via PR, then periodically `develop → main` via PR

### How CI Triggers (After Every Push)
- **Push to feature branch?** → CI does NOT run (features are not listed in ci.yml triggers)
- **PR from feature to develop?** → CI RUNS (build + test)
- **Push to develop (after PR merge)?** → CI RUNS (build + test)
- **PR from develop to main?** → CI RUNS (build + test)
- **Push to main (after PR merge)?** → CI RUNS (build + test + **DEPLOY to Render**)

So yes, CI runs on every meaningful merge. Both teammates see if their code breaks anything.

### Local Development Workflow (Day-to-Day)
During daily coding, you do NOT run full Docker every time:
1. Start only the DB container: `docker compose up db`
2. Run the Spring Boot app normally from IntelliJ (Run button)
3. App connects to PostgreSQL running in Docker on `localhost:5432`
4. Full Docker test (`docker compose up --build`) is done periodically for verification and before grading

---

## 🚀 Render Deployment Strategy

1. Create **Web Service** on Render, connect to GitHub repo
2. Build command: `./mvnw clean package -DskipTests`
3. Start command: `java -jar target/MIniMarketPlacePrototype-0.0.1-SNAPSHOT.jar`
4. Add **Render PostgreSQL** add-on (free tier)
5. Set environment variables in Render dashboard:
   - `SPRING_DATASOURCE_URL` = Render PostgreSQL connection URL
   - `SPRING_DATASOURCE_USERNAME` = from Render DB
   - `SPRING_DATASOURCE_PASSWORD` = from Render DB
   - `SPRING_JPA_HIBERNATE_DDL_AUTO` = `update`
6. Enable auto-deploy from `main` branch
7. Alternative: Use GitHub Actions deploy hook (webhook URL in GitHub Secrets)

---

## ⚠️ Automatic Failure Conditions (30% Penalty EACH)

| Condition | How We Avoid It | Status |
|---|---|---|
| No role-based access control | Spring Security + URL rules + @PreAuthorize | ✅ Planned |
| Direct push to main | Branch protection after CI bootstrap | ✅ Planned |
| No Dockerization | Dockerfile + docker-compose.yml | ✅ Planned |
| Tests not implemented | 17 unit + 5 integration tests | ✅ Planned |
| App not deployed | Render deployment + CI/CD | ✅ Planned |

---

## 📊 Marking Coverage Map

| Criterion | Marks | Mubin's Contribution | Teammate's Contribution |
|---|---|---|---|
| **Architecture & Code Quality** | 20 | User/Role entities, ProductService, AdminController, DTOs, Strategy pattern | Product/Order entities, UserService, OrderService, AuthController, exception handling, Factory pattern |
| **Security & Role Management** | 15 | SecurityConfig, BCryptEncoder, CustomUserDetailsService | Auth controller/views, role-restricted buyer/seller endpoints, security integration tests |
| **Testing** | 15 | 9 unit tests, 2 integration tests | 8 unit tests, 3 integration tests |
| **Dockerization** | 10 | Dockerfile, docker-compose.yml, .dockerignore, .env config | PR reviews |
| **CI/CD & Git Workflow** | 15 | CI workflow, branch protection, deploy job, Render setup, PR reviews | PR reviews |
| **Database Design** | 10 | User & Role entities + relationships | Product & Order entities + relationships |
| **Deployment & Demo** | 10 | Render service setup, PostgreSQL add-on, env vars, verification | Demo assistance |
| **Documentation** | 5 | README (architecture, ER, endpoints, setup, CI/CD), designpattern1.md | designpattern2.md, README (contribution section) |
| **TOTAL** | **100** | **Both contribute to every criterion** | **Both contribute to every criterion** |

---

## 📅 Sprint Timeline

| Sprint | Days | Focus | Key Deliverables |
|---|---|---|---|
| **Phase 0** | Day 1 | Git bootstrap, pom.xml fix, CI setup | Working CI, branch protection, develop branch |
| **Phase 1** | Days 2-4 | Entities, Repositories, Security | 4 JPA entities, Spring Security, BCrypt, DataInitializer |
| **Phase 2** | Days 5-8 | Services, Controllers, Views | 3 services, 4 controllers, Thymeleaf templates |
| **Phase 3** | Days 5-6 | Design Patterns (parallel) | Strategy + Factory patterns + documentation |
| **Phase 4** | Days 7-9 | Testing | 17 unit tests + 5 integration tests |
| **Phase 5** | Days 10-12 | Docker, CI/CD, Render, README | Full pipeline, live deployment, documentation |

---

## 📁 Target File Structure

```
MiniMarketPlace/
├── .github/workflows/ci.yml
├── .gitattributes
├── .gitignore
├── .dockerignore
├── .env.example
├── Dockerfile
├── docker-compose.yml
├── designpattern1.md              ← Mubin (Strategy)
├── designpattern2.md              ← Teammate (Factory)
├── PLANNING.md                    ← This file
├── ARCHITECTURE.md
├── WORK_DIVISION.md
├── README.md                      ← Created last
├── pom.xml
├── mvnw / mvnw.cmd
└── src/
    ├── main/
    │   ├── java/com/example/minimarketplaceprototype/
    │   │   ├── MIniMarketPlacePrototypeApplication.java
    │   │   ├── config/
    │   │   │   └── SecurityConfig.java
    │   │   ├── controller/
    │   │   │   ├── AuthController.java
    │   │   │   ├── ProductController.java
    │   │   │   ├── OrderController.java
    │   │   │   └── AdminController.java
    │   │   ├── dto/
    │   │   │   ├── UserRegistrationDto.java
    │   │   │   ├── ProductDto.java
    │   │   │   ├── OrderDto.java
    │   │   │   └── ErrorResponse.java
    │   │   ├── enums/
    │   │   │   └── RoleName.java
    │   │   ├── exception/
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   ├── DuplicateResourceException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── factory/                          ← Teammate's pattern
    │   │   │   ├── Notification.java
    │   │   │   ├── EmailNotification.java
    │   │   │   ├── SystemNotification.java
    │   │   │   └── NotificationFactory.java
    │   │   ├── model/
    │   │   │   ├── Role.java
    │   │   │   ├── User.java
    │   │   │   ├── Product.java
    │   │   │   └── Order.java
    │   │   ├── repository/
    │   │   │   ├── RoleRepository.java
    │   │   │   ├── UserRepository.java
    │   │   │   ├── ProductRepository.java
    │   │   │   └── OrderRepository.java
    │   │   ├── service/
    │   │   │   ├── UserService.java
    │   │   │   ├── UserServiceImpl.java
    │   │   │   ├── ProductService.java
    │   │   │   ├── ProductServiceImpl.java
    │   │   │   ├── OrderService.java
    │   │   │   ├── OrderServiceImpl.java
    │   │   │   └── CustomUserDetailsService.java
    │   │   └── strategy/                         ← Mubin's pattern
    │   │       ├── OrderPricingStrategy.java
    │   │       ├── RegularPricingStrategy.java
    │   │       └── DiscountPricingStrategy.java
    │   └── resources/
    │       ├── application.properties
    │       ├── application-test.properties        ← H2 for test profile
    │       ├── static/css/style.css
    │       └── templates/
    │           ├── layout.html
    │           ├── home.html
    │           ├── login.html
    │           ├── register.html
    │           ├── seller/
    │           │   ├── products.html
    │           │   └── product-form.html
    │           ├── buyer/
    │           │   ├── products.html
    │           │   ├── product-detail.html
    │           │   └── orders.html
    │           └── admin/
    │               ├── dashboard.html
    │               ├── users.html
    │               ├── products.html
    │               └── orders.html
    └── test/
        ├── java/com/example/minimarketplaceprototype/
        │   ├── service/
        │   │   ├── UserServiceTest.java
        │   │   ├── ProductServiceTest.java
        │   │   └── OrderServiceTest.java
        │   └── controller/
        │       ├── AuthControllerIntegrationTest.java
        │       ├── ProductControllerIntegrationTest.java
        │       └── AdminControllerIntegrationTest.java
        └── resources/
            └── application-test.properties
```

---

## ✅ Next Steps (In Order)

1. ✅ Read this PLANNING.md thoroughly (both members)
2. ✅ Read ARCHITECTURE.md for technical details
3. ✅ Read WORK_DIVISION.md for exact task assignments
4. ✅ Phase 0: Fix pom.xml, bootstrap CI, enable branch protection
5. ✅ Phase 1: Build entities + security
6. ✅ Phase 2: Build services + controllers + views
7. ✅ Phase 3: Implement design patterns
8. ✅ Phase 4: Write all tests
9. ✅ Phase 5: Docker + CI/CD + Render + README

**Version:** 1.0 | **Last Updated:** March 7, 2026

