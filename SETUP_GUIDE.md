# 🛠️ Day 0 Setup Guide — Both Machines

This guide ensures both Mubin (Windows) and Teammate (Mac) start from an identical, working foundation.

---

## Step 1: Install Prerequisites

### Both Machines

| Tool | Version | Download |
|---|---|---|
| Java JDK | **21 LTS (Temurin)** | https://adoptium.net/temurin/releases/?version=21 |
| Docker Desktop | Latest | https://www.docker.com/products/docker-desktop/ |
| Git | Latest | https://git-scm.com/downloads |
| IntelliJ IDEA | Community or Ultimate | https://www.jetbrains.com/idea/download/ |

### Verify Installation
```bash
java -version          # Must show: openjdk version "21.x.x"
docker --version       # Must show: Docker version 2x.x.x
docker compose version # Must show: Docker Compose version v2.x.x
git --version          # Must show: git version 2.x.x
```

---

## Step 2: IntelliJ Configuration (Both Machines)

1. **Project SDK:** File → Project Structure → Project → SDK = **21**
2. **Language Level:** File → Project Structure → Project → Language Level = **21**
3. **Line Separator:** Settings → Editor → Code Style → Line Separator = **Unix and macOS (\n)**
4. **Maven:** Settings → Build → Build Tools → Maven → Use Maven wrapper (should auto-detect `mvnw`)

---

## Step 3: Clone Repository

```bash
git clone https://github.com/MMI122/MiniMarketPlace.git
cd MiniMarketPlace
```

---

## Step 4: Verify Build

### Windows (Mubin)
```powershell
.\mvnw.cmd clean install
```

### Mac (Teammate)
```bash
chmod +x mvnw
./mvnw clean install
```

Build must succeed with `BUILD SUCCESS`.

---

## Step 5: Fix pom.xml (CRITICAL — Before Any Other Work)

The current pom.xml has several issues that must be fixed:

### Issues to Fix
1. **Java version 25 → 21** (Java 25 doesn't exist yet)
2. **Spring Boot 4.0.3 → 3.4.3** (4.x is bleeding-edge and requires Java 24+; 3.4.3 is the latest stable for Java 21)
3. **Remove `spring-boot-h2console`** (wrong artifact name)
4. **Remove fake test starters** (`spring-boot-starter-data-jpa-test`, `spring-boot-starter-thymeleaf-test`, `spring-boot-starter-webmvc-test` — these don't exist)
5. **Change `spring-boot-starter-webmvc` to `spring-boot-starter-web`**
6. **Keep H2 but only for test scope**
7. **Add `spring-boot-starter-test`** (includes JUnit 5 + Mockito)
8. **Add `spring-security-test`**
9. **Add `spring-boot-starter-validation`**

### Corrected pom.xml Properties & Dependencies
```xml
<properties>
    <java.version>21</java.version>
</properties>
```

See the actual pom.xml file for the complete corrected version.

---

## Step 6: Create/Verify .gitattributes

Must contain:
```
* text=auto
*.java text eol=lf
*.xml text eol=lf
*.yml text eol=lf
*.yaml text eol=lf
*.properties text eol=lf
*.html text eol=lf
*.css text eol=lf
*.md text eol=lf
/mvnw text eol=lf
*.cmd text eol=crlf
*.bat text eol=crlf
```

This prevents CRLF/LF conflicts between Windows and Mac.

---

## Step 7: Verify .gitignore

Must include:
```
target/
.idea/
*.iml
*.iws
*.ipr
.env
.DS_Store
```

---

## Step 8: Run with Docker (Final Verification)

```bash
docker compose up --build
```

App should start at: http://localhost:8080

---

## Step 9: Git Branch Setup

### Mubin does (one time):
```bash
# On main branch, after pom.xml fix and CI setup
git add .
git commit -m "chore: fix pom.xml, add CI workflow, configure project"
git push origin main

# Create develop branch
git checkout -b develop
git push -u origin develop
```

### Then enable branch protection on GitHub:
1. Go to: GitHub → Settings → Branches → Add rule
2. Branch name pattern: `main`
3. Enable:
   - ✅ Require a pull request before merging
   - ✅ Require at least 1 approval
   - ✅ Require status checks to pass before merging
   - ✅ Select the CI job name
4. Save

### Teammate does:
```bash
git clone https://github.com/MMI122/MiniMarketPlace.git
cd MiniMarketPlace
git checkout develop
git pull origin develop
# Verify build
./mvnw clean install
```

---

## Step 10: Docker Desktop Configuration

### Windows (Mubin)
- Enable WSL2 backend in Docker Desktop settings
- Allocate at least 4GB RAM to Docker

### Mac (Teammate)
- Docker Desktop should work out of the box
- Allocate at least 4GB RAM in Preferences → Resources

---

## ✅ Day 0 Checklist

| Task | Mubin | Teammate |
|---|---|---|
| Java 21 installed | ☐ | ☐ |
| Docker Desktop installed | ☐ | ☐ |
| IntelliJ configured (SDK 21, line sep \n) | ☐ | ☐ |
| Repository cloned | ☐ | ☐ |
| pom.xml fixed | ☐ | ☐ |
| `./mvnw clean install` passes | ☐ | ☐ |
| `.gitattributes` verified | ☐ | ☐ |
| `.gitignore` verified | ☐ | ☐ |
| CI workflow pushed to main | ☐ | — |
| Branch protection enabled | ☐ | — |
| develop branch created | ☐ | ☐ |
| Docker compose works | ☐ | ☐ |

---

## ⚠️ Troubleshooting

### "mvn: command not found" (Mac)
```bash
chmod +x mvnw
./mvnw clean install
```

### "Port 5432 already in use"
Another PostgreSQL instance is running. Stop it or change docker-compose port:
```yaml
ports:
  - "5433:5432"
```

### "BUILD FAILURE" after pom.xml changes
```bash
# Windows
.\mvnw.cmd clean install -U

# Mac
./mvnw clean install -U
```
The `-U` forces Maven to re-download dependencies.

### IntelliJ "cannot resolve symbol" after pom.xml changes
Right-click `pom.xml` → Maven → Reload Project

### "Do I need to add a DataSource in IntelliJ?"
**NO.** You do NOT need to configure IntelliJ's Database tool (the "DataSource" panel). PostgreSQL runs inside Docker. Spring Boot connects to it automatically via the environment variables in `application.properties`. IntelliJ DataSource is just an optional visual browser for tables — completely unnecessary for this project.

### "Do I need to install PostgreSQL on my machine?"
**NO.** PostgreSQL runs inside a Docker container. That's the whole point of Docker. Just run `docker compose up db` and the database is ready.

---

**Version:** 1.1 | **Last Updated:** March 7, 2026

