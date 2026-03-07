# 🔄 Git Workflow & CI/CD Guide

This document explains exactly how to set up and use the Git branching strategy, CI pipeline, and deployment workflow.

---

## 1. The Branch Protection Bootstrap Problem (SOLVED)

### The Problem
> "If I enable branch protection on `main` before `ci.yml` exists, I can't push `ci.yml` to `main`."

### The Solution (Industry Standard)
1. Push `ci.yml` to `main` **FIRST** (one-time direct push)
2. **THEN** enable branch protection
3. All future changes go through PRs

```
Timeline:
─────────────────────────────────────────────────
Day 1 (one time):
  main ← direct push (pom.xml fix + ci.yml + .gitattributes)
  main → create develop branch
  main → ENABLE branch protection
─────────────────────────────────────────────────
Day 2+:
  feature/* → PR → develop → PR → main
  (branch protection active, CI runs on every PR)
─────────────────────────────────────────────────
```

This is NOT cheating. This is how every company does it.

---

## 2. Branch Strategy

```
main (protected)
  │
  └── develop (integration branch)
        │
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

### Rules
- ❌ **NEVER push directly to `main`** (automatic failure: 30% penalty)
- ❌ **NEVER push directly to `develop`** (use PRs)
- ✅ Push to `feature/*` branches freely
- ✅ Merge `feature/*` → `develop` via PR (other person reviews)
- ✅ Merge `develop` → `main` via PR (CI must pass, other person approves)

---

## 3. Daily Git Workflow Commands

### Starting New Work
```bash
# Always start from latest develop
git checkout develop
git pull origin develop

# Create feature branch
git checkout -b feature/my-feature-name
```

### During Work
```bash
# Add and commit changes
git add .
git commit -m "feat: add product service CRUD operations"

# Push feature branch
git push origin feature/my-feature-name
```

### Creating Pull Request
1. Go to GitHub → your repo
2. Click "Compare & Pull Request" (GitHub shows this after push)
3. Set: `feature/my-feature-name` → `develop`
4. Add description of changes
5. Assign the **other teammate** as reviewer
6. Wait for review + CI pass
7. Merge after approval

### Keeping Feature Branch Updated
```bash
# If develop has new changes you need
git checkout feature/my-feature-name
git merge develop
# Resolve conflicts if any
git push origin feature/my-feature-name
```

### Merging develop → main (Periodic Releases)

> ⚠️ **STOP — Before creating this PR, follow `PRE_MERGE_TESTING.md` protocol first!**
> Both teammates must verify tests + direct run + Docker on their own machine.

1. Both pull latest `develop` and run full verification (see `PRE_MERGE_TESTING.md`)
2. Both confirm all tests pass + app works + Docker works on their machine
3. Create PR: `develop` → `main`
4. Paste both confirmation messages in PR description
5. CI runs automatically
6. Other teammate approves
7. Merge after CI passes
8. This triggers deployment to Render
9. Verify the live Render URL works

---

## 4. Commit Message Convention

```
feat: add user registration service
feat: create product CRUD endpoints
feat: implement strategy pattern for pricing
fix: correct BCrypt encoding in registration
fix: resolve product update authorization check
test: add UserService unit tests
test: add ProductController integration tests
docs: update PLANNING.md with test strategy
docs: create designpattern1.md
chore: update docker-compose configuration
chore: add .env.example
style: format SecurityConfig code
refactor: extract DTO mapping to helper method
```

### Format
```
<type>: <short description>

Types:
  feat:     New feature
  fix:      Bug fix
  test:     Adding/modifying tests
  docs:     Documentation only
  chore:    Build, config, CI changes
  style:    Code formatting (no logic change)
  refactor: Code change (no new feature or fix)
```

---

## 5. CI/CD Pipeline (GitHub Actions)

### File: `.github/workflows/ci.yml`

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ develop, main ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build and Test
        run: ./mvnw clean verify

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: target/surefire-reports/

  deploy:
    needs: build-and-test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main' && github.event_name == 'push'

    steps:
      - name: Deploy to Render
        run: |
          curl -X POST "${{ secrets.RENDER_DEPLOY_HOOK_URL }}"
```

### What Happens When
| Event | Trigger | Jobs Run |
|---|---|---|
| Push to `develop` | Code pushed | Build + Test only |
| PR to `develop` | PR opened/updated | Build + Test only |
| PR to `main` | PR opened/updated | Build + Test only |
| Push to `main` (after PR merge) | PR merged | Build + Test + **Deploy to Render** |

### Setting Up GitHub Secrets
1. GitHub → Settings → Secrets and variables → Actions
2. Add: `RENDER_DEPLOY_HOOK_URL` = (get from Render dashboard)

---

## 6. Branch Protection Setup (GitHub)

### Steps
1. Go to: https://github.com/MMI122/MiniMarketPlace/settings/branches
2. Click "Add branch protection rule"
3. Branch name pattern: `main`
4. Enable:
   - ✅ **Require a pull request before merging**
     - ✅ Required number of approvals: **1**
   - ✅ **Require status checks to pass before merging**
     - ✅ Require branches to be up to date before merging
     - Search and select: **build-and-test** (the CI job name)
   - ✅ **Do not allow bypassing the above settings**
5. Click "Create"

### After Protection is Active
- ❌ Cannot push directly to `main`
- ❌ Cannot merge PR without CI passing
- ❌ Cannot merge PR without 1 approval
- ✅ Must use PR workflow for all changes to `main`

---

## 6.1 When Does CI Actually Run?

This is important to understand. CI triggers based on the `ci.yml` config:

```
on:
  push:
    branches: [ develop, main ]
  pull_request:
    branches: [ main, develop ]
```

| What You Do | Does CI Run? | Why? |
|---|---|---|
| Push to `feature/xyz` | ❌ No | Feature branches are not in the trigger list |
| Open PR: `feature/xyz` → `develop` | ✅ Yes | `pull_request` to `develop` triggers CI |
| Merge PR into `develop` | ✅ Yes | `push` to `develop` triggers CI |
| Open PR: `develop` → `main` | ✅ Yes | `pull_request` to `main` triggers CI |
| Merge PR into `main` | ✅ Yes | `push` to `main` triggers CI + **Deploy** |

So every time either of you merges a feature, CI runs. Both of you see if your code breaks.

---

## 6.2 Local Development Workflow (Day-to-Day)

You do **NOT** need full Docker running while writing code:

```
Step 1: Start only PostgreSQL in Docker
   docker compose up db

Step 2: Run Spring Boot from IntelliJ
   Click the green Run button (Shift+F10)
   App connects to PostgreSQL on localhost:5432

Step 3: Test in browser
   http://localhost:8080

Step 4: Before creating a PR, verify full build
   Windows: .\mvnw.cmd clean verify "-Dspring.profiles.active=test"
   Mac:     ./mvnw clean verify -Dspring.profiles.active=test
```

**Full Docker test** (periodically, before PRs to `main`):
```
docker compose up --build
```
This builds the app container + DB container together, exactly like the grader will test it.

**You do NOT need IntelliJ DataSource.** PostgreSQL runs in Docker. Spring Boot connects automatically via `application.properties` env vars.

---

## 7. Render Deployment Setup

### Create Web Service
1. Go to https://render.com → Dashboard → New → Web Service
2. Connect your GitHub repository
3. Configure:
   - **Name:** minimarketplace
   - **Region:** Choose closest
   - **Branch:** `main`
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/MIniMarketPlacePrototype-0.0.1-SNAPSHOT.jar`
   - **Plan:** Free

### Create PostgreSQL Database
1. Render Dashboard → New → PostgreSQL
2. Configure:
   - **Name:** minimarketplace-db
   - **Plan:** Free
3. After creation, copy the "Internal Database URL"

### Set Environment Variables on Render
| Variable | Value |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://[Render DB internal URL]` |
| `SPRING_DATASOURCE_USERNAME` | (from Render DB dashboard) |
| `SPRING_DATASOURCE_PASSWORD` | (from Render DB dashboard) |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` |
| `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT` | `org.hibernate.dialect.PostgreSQLDialect` |

### Get Deploy Hook URL
1. Render → Your Web Service → Settings → Deploy Hook
2. Copy the webhook URL
3. Add it as GitHub Secret: `RENDER_DEPLOY_HOOK_URL`

### Verify Deployment
After first merge to `main`:
1. Check GitHub Actions → deploy job should succeed
2. Check Render dashboard → deployment should be "Live"
3. Visit your Render URL → app should load

---

## 8. Complete Workflow Example

```
Mubin creates a feature:
  git checkout develop
  git pull origin develop
  git checkout -b feature/product-service
  
  ... writes ProductService code ...
  
  git add .
  git commit -m "feat: add ProductService with CRUD operations"
  git push origin feature/product-service
  
  → Goes to GitHub
  → Creates PR: feature/product-service → develop
  → Assigns Teammate as reviewer
  
Teammate reviews:
  → Reads code changes
  → Leaves comments or approves
  → Clicks "Approve"
  
Mubin merges:
  → CI passes ✅
  → Teammate approved ✅
  → Click "Merge pull request"
  
Periodically:
  → Create PR: develop → main
  → Both review
  → CI passes
  → Merge
  → GitHub Actions deploys to Render automatically
```

---

## 9. Emergency: If Someone Accidentally Pushes to Main

If branch protection isn't set up yet and someone pushes directly:
1. The commit history will show it
2. The grader will see it
3. **30% penalty**

**Solution:** Set up branch protection on Day 1 (after CI bootstrap) and never remove it.

---

**Version:** 1.0 | **Last Updated:** March 7, 2026

