# 🧪 Pre-Main Merge Testing Protocol

**Purpose:** Before ANY merge from `develop` → `main`, both Mubin (Windows) and Teammate (Mac) must verify the app works on their machine. This ensures nothing breaks in production.

---

## Why This Matters

- `main` is protected and auto-deploys to Render
- If broken code reaches `main`, the live app breaks
- The grader will check the live Render URL
- Testing on `develop` first catches Windows/Mac compatibility issues early

---

## The Process (Step by Step)

### When to Run This Protocol

Run this **every time** before creating a PR from `develop` → `main`.

Do NOT run this for `feature` → `develop` merges (those are tested by CI automatically).

---

### Step 1: Both Pull Latest `develop`

**Mubin (Windows):**
```powershell
git checkout develop
git pull origin develop
```

**Teammate (Mac):**
```bash
git checkout develop
git pull origin develop
```

Both machines now have identical code.

---

### Step 2: Run Tests (Both Machines)

**Mubin (Windows):**
```powershell
.\mvnw.cmd clean verify "-Dspring.profiles.active=test"
```

**Teammate (Mac):**
```bash
./mvnw clean verify -Dspring.profiles.active=test
```

✅ **Expected:** `BUILD SUCCESS`, all tests pass (0 failures, 0 errors)

If any test fails → fix it on a `feature/fix-xyz` branch → merge to `develop` → repeat this step.

---

### Step 3: Run App Directly from IntelliJ (Both Machines)

First, start only the database:
```bash
docker compose up db
```

Then run the Spring Boot app from IntelliJ (green Run button / Shift+F10).

✅ **Check on both machines:**
- [ ] App starts without errors in console
- [ ] `http://localhost:8080` loads in browser
- [ ] Registration page works (`/register`)
- [ ] Login page works (`/login`)
- [ ] Can register as SELLER and see seller pages
- [ ] Can register as BUYER and see buyer pages
- [ ] Admin login works (pre-seeded admin account)
- [ ] Role-based access enforced (buyer can't access `/seller/**`, etc.)

Stop the app and the db container when done:
```bash
docker compose down
```

---

### Step 4: Run Full Docker (Both Machines)

This simulates exactly how the grader will test your project.

```bash
docker compose up --build
```

✅ **Check on both machines:**
- [ ] Both containers start (db + app)
- [ ] No errors in Docker logs
- [ ] `http://localhost:8080` loads in browser
- [ ] Same functionality checks as Step 3

Stop when done:
```bash
docker compose down
```

---

### Step 5: Confirm Results (Communication)

Both teammates confirm results via message. Use this exact format:

```
MUBIN (Windows):
  Tests:  ✅ PASS (17 unit + 5 integration, 0 failures)
  Direct: ✅ App runs, all pages load, roles enforced
  Docker: ✅ docker compose up --build works, app accessible

TEAMMATE (Mac):
  Tests:  ✅ PASS (17 unit + 5 integration, 0 failures)
  Direct: ✅ App runs, all pages load, roles enforced
  Docker: ✅ docker compose up --build works, app accessible
```

**Only proceed to Step 6 if BOTH show all ✅.**

If anything fails → fix on a feature branch → merge to develop → restart from Step 1.

---

### Step 6: Create PR from `develop` → `main`

Only after both teammates confirmed:

1. Go to GitHub → Pull Requests → New Pull Request
2. Base: `main` ← Compare: `develop`
3. Title: `release: merge develop to main (both machines verified)`
4. Description: Paste both confirmation messages from Step 5
5. Assign other teammate as reviewer
6. Other teammate approves
7. CI runs and passes
8. Merge

This triggers automatic deployment to Render.

---

### Step 7: Verify Render Deployment

After merge to `main`:
1. Check GitHub Actions → deploy job should succeed
2. Wait 2-3 minutes for Render to build
3. Visit the live Render URL
4. Do a quick smoke test:
   - [ ] Home page loads
   - [ ] Can register a new user
   - [ ] Can login
   - [ ] Role-based pages work

---

## Quick Reference Checklist

Use this checklist every time before merging `develop` → `main`:

```
PRE-MAIN MERGE CHECKLIST
─────────────────────────────────────────
[ ] Both pulled latest develop
[ ] Mubin: tests pass (mvnw clean verify)
[ ] Teammate: tests pass (mvnw clean verify)
[ ] Mubin: app runs directly from IntelliJ
[ ] Teammate: app runs directly from IntelliJ
[ ] Mubin: docker compose up --build works
[ ] Teammate: docker compose up --build works
[ ] Both confirmed results to each other
[ ] PR created: develop → main
[ ] Other person approved PR
[ ] CI passed
[ ] PR merged
[ ] Render deployment verified
─────────────────────────────────────────
```

---

## Troubleshooting

### "Tests pass on my machine but fail on teammate's"
- Check Java version: `java -version` (must be 21 on both)
- Check line endings: ensure `.gitattributes` has `* text=auto`
- Run `git checkout develop` then `git pull origin develop` again (make sure code is identical)

### "Docker works on one machine but not the other"
- Check Docker Desktop is running
- Check port 5432 is not in use: `docker ps` (no other postgres container)
- Try `docker compose down -v` then `docker compose up --build` (fresh start with clean volume)

### "App starts but pages look different on Windows vs Mac"
- This is usually a Thymeleaf template issue, not an OS issue
- Check browser console for errors
- Ensure both are using the same browser for testing

### "Tests pass locally but CI fails"
- CI uses `ubuntu-latest` with Java 21 (Temurin)
- CI uses H2 test profile (same as local `./mvnw clean verify -Dspring.profiles.active=test`)
- Check the GitHub Actions log for the exact error message

---

**Version:** 1.0 | **Last Updated:** March 7, 2026

