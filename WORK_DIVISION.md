# 👥 Mini Marketplace — Work Division

**Team:** Mubin (Windows) + Teammate (MacBook)  
**Principle:** Both members contribute to **every** marking criterion. Work is divided by **feature slices**.

---

## 🎯 Division Principle

Both members must have visible GitHub contributions (commits, PRs, reviews) in:
- Architecture (entities, services, controllers)
- Security (config, role-based endpoints)
- Testing (unit tests AND integration tests)
- Documentation (design pattern doc + README sections)

---

## 📋 Phase 0: Foundation & Git Bootstrap (Day 1)

### Mubin's Tasks
- [ ] Fix `pom.xml` (Java 21, Spring Boot 3.4.3, correct dependencies)
- [ ] Create/update `.gitattributes` (`* text=auto`)
- [ ] Create `.github/workflows/ci.yml` (basic build + test)
- [ ] Create `Dockerfile` (multi-stage build)
- [ ] Create `.dockerignore`
- [ ] Update `compose.yaml` (app + PostgreSQL services)
- [ ] Create `.env.example` (template for env vars)
- [ ] Update `application.properties` (env var placeholders)
- [ ] Create `application-test.properties` (H2 for tests)
- [ ] Push everything to `main` (one-time direct push — this includes ci.yml)
- [ ] Create `develop` branch from `main`
- [ ] Enable branch protection on `main` (require PR + 1 approval + CI pass)

### Teammate's Tasks
- [ ] Install Java 21 (Temurin) on Mac
- [ ] Install Docker Desktop on Mac
- [ ] Clone repo on Mac
- [ ] Verify build works: `./mvnw clean install`
- [ ] Verify Docker works: `docker compose up --build`

### Both
- [ ] Set IntelliJ SDK to 21
- [ ] Set IntelliJ line separator to `\n` (Unix)

---

## 📋 Phase 1: Entities, Repositories & Security (Days 2-4)

### Mubin → Branch: `feature/user-role-entities`
- [ ] Create `RoleName.java` enum (ROLE_ADMIN, ROLE_SELLER, ROLE_BUYER)
- [ ] Create `Role.java` entity
- [ ] Create `User.java` entity (BCrypt password, Many-to-One to Role)
- [ ] Create `RoleRepository.java`
- [ ] Create `UserRepository.java`
- [ ] Create `UserRegistrationDto.java`
- [ ] Create `DataInitializer.java` (seed roles + admin user on startup)
- [ ] PR → develop (Teammate reviews)

### Teammate → Branch: `feature/product-order-entities`
- [ ] Create `Product.java` entity (Many-to-One to User/seller)
- [ ] Create `Order.java` entity (Many-to-One to User/buyer, Many-to-One to Product)
- [ ] Create `ProductRepository.java`
- [ ] Create `OrderRepository.java`
- [ ] Create `ProductDto.java`
- [ ] Create `OrderDto.java`
- [ ] PR → develop (Mubin reviews)

### Mubin → Branch: `feature/security-config`
- [ ] Create `SecurityConfig.java` (BCrypt bean, filter chain, URL-based role rules)
- [ ] Create `CustomUserDetailsService.java`
- [ ] PR → develop (Teammate reviews)

### Teammate → Branch: `feature/exception-handling`
- [ ] Create `ResourceNotFoundException.java`
- [ ] Create `DuplicateResourceException.java`
- [ ] Create `ErrorResponse.java` DTO
- [ ] Create `GlobalExceptionHandler.java` (@ControllerAdvice)
- [ ] PR → develop (Mubin reviews)

---

## 📋 Phase 2: Services, Controllers & Views (Days 5-8)

### Mubin → Branch: `feature/product-service-controller`
- [ ] Create `ProductService.java` (interface)
- [ ] Create `ProductServiceImpl.java` (create, read, update, delete, getBySellerProducts)
- [ ] Create `ProductController.java` (all seller endpoints + buyer browse)
- [ ] Create Thymeleaf templates:
  - `seller/products.html` (seller's product list)
  - `seller/product-form.html` (create/edit form)
  - `buyer/products.html` (browse all products)
  - `buyer/product-detail.html` (product detail view)
- [ ] PR → develop (Teammate reviews)

### Teammate → Branch: `feature/auth-service-controller`
- [ ] Create `UserService.java` (interface)
- [ ] Create `UserServiceImpl.java` (register, findByUsername, findAll)
- [ ] Create `AuthController.java` (login page, register form + processing)
- [ ] Create Thymeleaf templates:
  - `login.html`
  - `register.html`
  - `home.html` (landing page)
  - `layout.html` (Thymeleaf layout with navbar, Bootstrap CDN)
- [ ] PR → develop (Mubin reviews)

### Teammate → Branch: `feature/order-service-controller`
- [ ] Create `OrderService.java` (interface)
- [ ] Create `OrderServiceImpl.java` (placeOrder, getByBuyer, calculateTotal)
- [ ] Create `OrderController.java` (buy now, view orders)
- [ ] Create Thymeleaf templates:
  - `buyer/orders.html` (buyer's order list)
- [ ] PR → develop (Mubin reviews)

### Mubin → Branch: `feature/admin-controller`
- [ ] Create `AdminController.java` (dashboard, users list, products list, orders list, delete user)
- [ ] Create Thymeleaf templates:
  - `admin/dashboard.html`
  - `admin/users.html`
  - `admin/products.html`
  - `admin/orders.html`
- [ ] PR → develop (Teammate reviews)

---

## 📋 Phase 3: Design Patterns (Days 5-6, parallel with Phase 2)

### Mubin → Branch: `feature/strategy-pattern`
- [ ] Create `OrderPricingStrategy.java` (interface)
- [ ] Create `RegularPricingStrategy.java` (implements interface)
- [ ] Create `DiscountPricingStrategy.java` (implements interface)
- [ ] Integrate into `OrderServiceImpl` (inject strategy via constructor)
- [ ] Update `designpattern1.md` with final code references
- [ ] PR → develop (Teammate reviews)

### Teammate → Branch: `feature/factory-pattern`
- [ ] Create `Notification.java` (interface)
- [ ] Create `EmailNotification.java` (implements interface)
- [ ] Create `SystemNotification.java` (implements interface)
- [ ] Create `NotificationFactory.java` (factory class)
- [ ] Integrate into `OrderServiceImpl` (call after order placed)
- [ ] Update `designpattern2.md` with final code references
- [ ] PR → develop (Mubin reviews)

---

## 📋 Phase 4: Testing (Days 7-9)

### Mubin → Branch: `feature/unit-tests-mubin`
- [ ] `UserServiceTest`:
  - `registerUser_success`
  - `findByUsername_found`
- [ ] `ProductServiceTest`:
  - `getAllProducts_returnsList`
  - `getProductById_notFound_throws`
  - `updateProduct_success`
  - `getProductsBySeller_returnsList`
- [ ] `OrderServiceTest`:
  - `placeOrder_productNotFound_throws`
  - `calculateTotal_regularPricing`
  - `calculateTotal_discountPricing`
- **Total: 9 unit tests**
- [ ] PR → develop (Teammate reviews)

### Teammate → Branch: `feature/unit-tests-teammate`
- [ ] `UserServiceTest`:
  - `registerUser_duplicateUsername_throws`
  - `findByUsername_notFound_throws`
- [ ] `ProductServiceTest`:
  - `createProduct_success`
  - `getProductById_found`
  - `deleteProduct_success`
- [ ] `OrderServiceTest`:
  - `placeOrder_success`
  - `getOrdersByBuyer_returnsList`
  - `placeOrder_selfPurchase_throws`
- **Total: 8 unit tests**
- [ ] PR → develop (Mubin reviews)

### Mubin → Branch: `feature/integration-tests-mubin`
- [ ] `ProductControllerIntegrationTest`:
  - `productList_authenticatedSeller_returns200`
  - `productList_unauthenticated_redirectsToLogin`
- **Total: 2 integration tests**
- [ ] PR → develop (Teammate reviews)

### Teammate → Branch: `feature/integration-tests-teammate`
- [ ] `AuthControllerIntegrationTest`:
  - `registerPage_returns200`
  - `registerUser_redirectsToLogin`
- [ ] `AdminControllerIntegrationTest`:
  - `adminDashboard_withBuyerRole_returns403`
- **Total: 3 integration tests**
- [ ] PR → develop (Mubin reviews)

### Test Totals
| | Mubin | Teammate | Total |
|---|---|---|---|
| Unit Tests | 9 | 8 | **17** ✅ (min 15) |
| Integration Tests | 2 | 3 | **5** ✅ (min 3) |
| **Total** | **11** | **11** | **22** |

---

## 📋 Phase 5: Docker, CI/CD, Deployment & Docs (Days 10-12)

### Mubin → Branch: `feature/docker-setup`
- [ ] Finalize `Dockerfile` for production
- [ ] Finalize `compose.yaml` (app + PostgreSQL services)
- [ ] Finalize `.env.example`
- [ ] Test full stack locally: `docker compose up --build`
- [ ] PR → develop (Teammate reviews)

### Mubin → Branch: `feature/cicd-deploy`
- [ ] Finalize `.github/workflows/ci.yml`:
  - Build + test job
  - Deploy job (Render webhook on push to main)
- [ ] PR → develop (Teammate reviews)

### Mubin → Branch: `feature/render-setup`
- [ ] Create Render Web Service (connected to GitHub)
- [ ] Add Render PostgreSQL add-on
- [ ] Set environment variables in Render dashboard
- [ ] Verify deployment works on live URL

### Both → Branch: `feature/readme`
**Mubin writes:**
- [ ] Project description
- [ ] Architecture diagram section (placeholder for figure)
- [ ] ER diagram section (placeholder for figure)
- [ ] API endpoints table
- [ ] Design patterns section
- [ ] Run instructions (local + Docker)
- [ ] CI/CD explanation
- [ ] Deployed URL

**Teammate writes:**
- [ ] Contribution matrix
- [ ] Troubleshooting section

---

## 📊 Contribution Matrix (for README)

| Marking Criterion (marks) | Mubin | Teammate |
|---|---|---|
| **Architecture & Code Quality (20)** | User/Role entities, ProductService, ProductController, AdminController, DTOs, Strategy pattern | Product/Order entities, UserService, OrderService, AuthController, exception handling, Factory pattern |
| **Security & Role Management (15)** | SecurityConfig, BCryptEncoder, CustomUserDetailsService, admin endpoint security | Auth controller/views, role-restricted buyer/seller endpoints, security integration tests |
| **Testing (15)** | 9 unit tests + 2 integration tests | 8 unit tests + 3 integration tests |
| **Dockerization (10)** | Dockerfile, docker-compose.yml, .dockerignore, .env config | PR reviews |
| **CI/CD & Git Workflow (15)** | CI workflow, branch protection, deploy job, Render setup, PR reviews | PR reviews |
| **Database Design (10)** | User & Role entities + relationships | Product & Order entities + relationships |
| **Deployment & Demo (10)** | Render setup, PostgreSQL add-on, env vars, verification | Demo assistance |
| **Documentation (5)** | README (architecture, ER, endpoints, setup, CI/CD), designpattern1.md | designpattern2.md, README (contribution) |

---

## 🔄 Pull Request Workflow (Every Feature)

```
1. Create feature branch from develop:
   git checkout develop
   git pull origin develop
   git checkout -b feature/my-feature

2. Code + commit + push:
   git add .
   git commit -m "feat: description of change"
   git push origin feature/my-feature

3. Open PR on GitHub:
   feature/my-feature → develop

4. Other teammate reviews:
   - Check code quality
   - Check naming conventions
   - Check tests if applicable
   - Approve or request changes

5. Merge PR after approval

6. Periodically merge develop → main via PR:
   develop → main (CI runs, then merge → auto-deploys to Render)
```

### Commit Message Convention
```
feat: add user registration service
fix: correct BCrypt encoding in registration
test: add UserService unit tests
docs: update PLANNING.md with test strategy
chore: update docker-compose configuration
```

---

## 🖥️ Local Development Workflow (Day-to-Day)

During normal coding, you do NOT run full Docker every time:

```
1. Start only the DB container:
   docker compose up db

2. Run Spring Boot from IntelliJ:
   Click the green Run button (or Shift+F10)
   App connects to PostgreSQL on localhost:5432

3. Full Docker test (periodically, before PRs to main):
   docker compose up --build
   Verify everything works end-to-end
```

No IntelliJ DataSource configuration is needed. PostgreSQL runs in Docker.
Spring Boot connects to it automatically via the environment variables in `application.properties`.

---

## ⚡ Quick Reference: Who Does What

### Mubin's Files (Primary Author)
```
# Entities & Repos
model/Role.java, model/User.java
enums/RoleName.java
repository/RoleRepository.java, repository/UserRepository.java
dto/UserRegistrationDto.java

# Security
config/SecurityConfig.java
service/CustomUserDetailsService.java
DataInitializer.java

# Services & Controllers
service/ProductService.java, service/ProductServiceImpl.java
controller/ProductController.java, controller/AdminController.java

# Design Pattern
strategy/OrderPricingStrategy.java, strategy/RegularPricingStrategy.java, strategy/DiscountPricingStrategy.java
designpattern1.md

# Templates
templates/seller/*, templates/buyer/products.html, templates/buyer/product-detail.html, templates/admin/*

# Infrastructure
Dockerfile, .dockerignore, compose.yaml, .env.example
.github/workflows/ci.yml
.gitattributes, application.properties, application-test.properties

# Tests
9 unit tests + 2 integration tests
```

### Teammate's Files (Primary Author)
```
# Entities & Repos
model/Product.java, model/Order.java
repository/ProductRepository.java, repository/OrderRepository.java
dto/ProductDto.java, dto/OrderDto.java, dto/ErrorResponse.java

# Exception Handling
exception/ResourceNotFoundException.java, exception/DuplicateResourceException.java, exception/GlobalExceptionHandler.java

# Services & Controllers
service/UserService.java, service/UserServiceImpl.java
service/OrderService.java, service/OrderServiceImpl.java
controller/AuthController.java, controller/OrderController.java

# Design Pattern
factory/Notification.java, factory/EmailNotification.java, factory/SystemNotification.java, factory/NotificationFactory.java
designpattern2.md

# Templates
templates/login.html, templates/register.html, templates/home.html, templates/layout.html, templates/buyer/orders.html

# Tests
8 unit tests + 3 integration tests
```

---

## 📌 Important Rules

1. **Never push directly to `main`** (automatic failure condition)
2. **Always create PR** with the other person as reviewer
3. **Always pull `develop`** before creating new feature branch
4. **Run `./mvnw clean verify` locally** before pushing (or `.\mvnw.cmd clean verify` on Windows)
5. **Both members must have commits** in every sprint
6. **Use meaningful commit messages** (see convention above)
7. **Periodically test with full Docker:** `docker compose up --build`
8. **No IntelliJ DataSource needed** — PostgreSQL runs in Docker, Spring Boot connects via env vars automatically

---

**Version:** 1.1 | **Last Updated:** March 7, 2026
