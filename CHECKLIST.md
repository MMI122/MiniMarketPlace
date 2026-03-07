# ✅ Mini Marketplace — Master Checklist

Use this checklist to track progress. Both members should tick off items they've completed.

---

## ⚠️ AUTOMATIC FAILURE CONDITIONS (CHECK FIRST)

- [ ] Role-based access control implemented (ADMIN/SELLER/BUYER enforced)
- [ ] No direct push to main (branch protection is ON)
- [ ] Dockerization complete (Dockerfile + docker-compose.yml works)
- [ ] Tests implemented (15+ unit tests, 3+ integration tests)
- [ ] App deployed on Render (publicly accessible URL)

> **If ANY of these fail → 30% mark penalty EACH**

---

## Phase 0: Foundation (Day 1)

### Mubin
- [x] Fix pom.xml: Java 21, Spring Boot 3.4.3, correct dependencies
- [x] Create/update .gitattributes (cross-platform line endings)
- [x] Create .github/workflows/ci.yml
- [x] Create Dockerfile (multi-stage build)
- [x] Create .dockerignore
- [x] Update application.properties (env var placeholders)
- [ ] Push all to main (one-time direct push)
- [ ] Create develop branch from main
- [ ] Enable branch protection on main in GitHub Settings

### Teammate
- [ ] Clone repo, verify build: `./mvnw clean install`
- [x] application-test.properties created (H2 for tests)
- [ ] Verify Docker compose works locally

---

## Phase 1: Entities, Repos & Security (Days 2-4)

### Mubin → `feature/user-role-entities`
- [ ] RoleName.java (enum)
- [ ] Role.java (entity)
- [ ] User.java (entity with BCrypt password)
- [ ] RoleRepository.java
- [ ] UserRepository.java
- [ ] UserRegistrationDto.java
- [ ] DataInitializer.java (seed roles + admin)
- [ ] PR → develop, Teammate reviews

### Teammate → `feature/product-order-entities`
- [ ] Product.java (entity)
- [ ] Order.java (entity)
- [ ] ProductRepository.java
- [ ] OrderRepository.java
- [ ] ProductDto.java
- [ ] OrderDto.java
- [ ] PR → develop, Mubin reviews

### Mubin → `feature/security-config`
- [ ] SecurityConfig.java (BCrypt, filter chain, URL rules)
- [ ] CustomUserDetailsService.java
- [ ] PR → develop, Teammate reviews

### Teammate → `feature/exception-handling`
- [ ] ResourceNotFoundException.java
- [ ] DuplicateResourceException.java
- [ ] ErrorResponse.java
- [ ] GlobalExceptionHandler.java
- [ ] PR → develop, Mubin reviews

---

## Phase 2: Services, Controllers & Views (Days 5-8)

### Mubin → `feature/product-service-controller`
- [ ] ProductService.java (interface)
- [ ] ProductServiceImpl.java (CRUD + getBySellerProducts)
- [ ] ProductController.java (seller CRUD endpoints + buyer browse)
- [ ] seller/products.html
- [ ] seller/product-form.html
- [ ] buyer/products.html
- [ ] buyer/product-detail.html
- [ ] PR → develop, Teammate reviews

### Teammate → `feature/auth-service-controller`
- [ ] UserService.java (interface)
- [ ] UserServiceImpl.java (register, findByUsername, findAll)
- [ ] AuthController.java (login/register)
- [ ] layout.html (shared template)
- [ ] login.html
- [ ] register.html
- [ ] home.html
- [ ] PR → develop, Mubin reviews

### Teammate → `feature/order-service-controller`
- [ ] OrderService.java (interface)
- [ ] OrderServiceImpl.java (placeOrder, getByBuyer)
- [ ] OrderController.java (buy now, view orders)
- [ ] buyer/orders.html
- [ ] PR → develop, Mubin reviews

### Mubin → `feature/admin-controller`
- [ ] AdminController.java (dashboard, lists, delete user)
- [ ] admin/dashboard.html
- [ ] admin/users.html
- [ ] admin/products.html
- [ ] admin/orders.html
- [ ] PR → develop, Teammate reviews

---

## Phase 3: Design Patterns (Days 5-6)

### Mubin → `feature/strategy-pattern`
- [ ] OrderPricingStrategy.java (interface)
- [ ] RegularPricingStrategy.java
- [ ] DiscountPricingStrategy.java
- [ ] Integrate into OrderServiceImpl
- [x] designpattern1.md (documentation)
- [ ] PR → develop, Teammate reviews

### Teammate → `feature/factory-pattern`
- [ ] Notification.java (interface)
- [ ] EmailNotification.java
- [ ] SystemNotification.java
- [ ] NotificationFactory.java
- [ ] Integrate into OrderServiceImpl
- [x] designpattern2.md (documentation)
- [ ] PR → develop, Mubin reviews

---

## Phase 4: Testing (Days 7-9)

### Mubin → `feature/unit-tests-mubin` (9 tests)
- [ ] UserServiceTest: registerUser_success
- [ ] UserServiceTest: findByUsername_found
- [ ] ProductServiceTest: getAllProducts_returnsList
- [ ] ProductServiceTest: getProductById_notFound_throws
- [ ] ProductServiceTest: updateProduct_success
- [ ] ProductServiceTest: getProductsBySeller_returnsList
- [ ] OrderServiceTest: placeOrder_productNotFound_throws
- [ ] OrderServiceTest: calculateTotal_regularPricing
- [ ] OrderServiceTest: calculateTotal_discountPricing
- [ ] PR → develop, Teammate reviews

### Teammate → `feature/unit-tests-teammate` (8 tests)
- [ ] UserServiceTest: registerUser_duplicateUsername_throws
- [ ] UserServiceTest: findByUsername_notFound_throws
- [ ] ProductServiceTest: createProduct_success
- [ ] ProductServiceTest: getProductById_found
- [ ] ProductServiceTest: deleteProduct_success
- [ ] OrderServiceTest: placeOrder_success
- [ ] OrderServiceTest: getOrdersByBuyer_returnsList
- [ ] OrderServiceTest: placeOrder_selfPurchase_throws
- [ ] PR → develop, Mubin reviews

### Mubin → `feature/integration-tests-mubin` (2 tests)
- [ ] ProductControllerIntegrationTest: productList_authenticatedSeller_returns200
- [ ] ProductControllerIntegrationTest: productList_unauthenticated_redirectsToLogin
- [ ] PR → develop, Teammate reviews

### Teammate → `feature/integration-tests-teammate` (3 tests)
- [ ] AuthControllerIntegrationTest: registerPage_returns200
- [ ] AuthControllerIntegrationTest: registerUser_redirectsToLogin
- [ ] AdminControllerIntegrationTest: adminDashboard_withBuyerRole_returns403
- [ ] PR → develop, Mubin reviews

### Verify
- [ ] All 17 unit tests pass: `./mvnw test`
- [ ] All 5 integration tests pass
- [ ] CI pipeline runs all tests successfully (check GitHub Actions)

---

## Phase 5: Docker, CI/CD & Deployment (Days 10-12)

### Mubin
- [x] Dockerfile created (multi-stage build)
- [x] .dockerignore created
- [x] docker-compose.yml updated (app + db services)
- [x] .env.example created
- [ ] Test Docker build: `docker build -t minimarket .`
- [ ] Test full stack: `docker compose up --build`
- [ ] Finalize ci.yml with deploy job
- [ ] Add RENDER_DEPLOY_HOOK_URL to GitHub Secrets
- [ ] Create Render Web Service
- [ ] Create Render PostgreSQL add-on
- [ ] Set Render env vars
- [ ] Verify live URL works
- [ ] Verify auto-deploy works (push to main → Render deploys)

### Both
- [ ] Full pipeline test: feature → develop → main → Render
- [ ] Live application accessible at Render URL

---

## ⚠️ Before EVERY develop → main Merge (See `PRE_MERGE_TESTING.md`)

- [ ] Both pulled latest `develop`
- [ ] Mubin: `.\mvnw.cmd clean verify "-Dspring.profiles.active=test"` → BUILD SUCCESS
- [ ] Teammate: `./mvnw clean verify -Dspring.profiles.active=test` → BUILD SUCCESS
- [ ] Mubin: app runs directly from IntelliJ (`docker compose up db` + Run)
- [ ] Teammate: app runs directly from IntelliJ (`docker compose up db` + Run)
- [ ] Mubin: `docker compose up --build` → app works at localhost:8080
- [ ] Teammate: `docker compose up --build` → app works at localhost:8080
- [ ] Both confirmed results to each other
- [ ] PR created: develop → main (with confirmation messages in description)
- [ ] Other person approved PR
- [ ] CI passed
- [ ] Merged
- [ ] Render deployment verified

---

## Phase 6: README (Last Thing)

### Both → `feature/readme`
- [ ] Project description
- [ ] Architecture diagram (placeholder for image)
- [ ] ER diagram (placeholder for image)
- [ ] API endpoints table
- [ ] Run instructions (local + Docker)
- [ ] CI/CD explanation
- [ ] Deployed URL
- [ ] Contribution matrix
- [ ] PR → develop → main

---

## 📊 Test Count Summary

| Category | Mubin | Teammate | Total | Required |
|---|---|---|---|---|
| Unit Tests | 9 | 8 | **17** | 15 ✅ |
| Integration Tests | 2 | 3 | **5** | 3 ✅ |
| **Total** | **11** | **11** | **22** | 18 ✅ |

---

## 📁 Files Created/Modified So Far

### ✅ Already Done (Phase 0)
```
✅ pom.xml                    — Fixed (Java 21, Boot 3.4.3, correct deps)
✅ .gitattributes              — Cross-platform line endings
✅ .gitignore                  — Updated with .env, .DS_Store
✅ application.properties      — Env var placeholders for DB
✅ application-test.properties — H2 for tests (in test/resources)
✅ Dockerfile                  — Multi-stage build
✅ .dockerignore               — Exclude non-essential files
✅ compose.yaml                — App + PostgreSQL services
✅ .env.example                — Template for env vars
✅ .github/workflows/ci.yml    — Build + Test + Deploy pipeline
✅ PLANNING.md                 — Complete project plan
✅ ARCHITECTURE.md             — Technical architecture
✅ WORK_DIVISION.md            — Task assignments
✅ GIT_WORKFLOW.md             — Branch & CI/CD guide
✅ SETUP_GUIDE.md              — Machine setup instructions
✅ designpattern1.md           — Strategy pattern docs
✅ designpattern2.md           — Factory pattern docs
✅ PRE_MERGE_TESTING.md        — Pre-main merge verification protocol
✅ CHECKLIST.md                — This file
```

### 🔜 Still Needed (Phases 1-6)
```
All Java source files (entities, services, controllers, etc.)
All Thymeleaf HTML templates
All test classes
README.md (created last)
```

