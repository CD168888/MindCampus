# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MindCampus is a mental health intelligent intervention platform for college students. It's built on Spring Boot 3.5.4 + MyBatis Plus + Vue 3 + Uni-app, with AI capabilities powered by Alibaba DashScope (Tongyi Qianwen).

## Build & Run Commands

### Backend (Java 17 + Maven)

```bash
# Clean the project
mvn clean

# Build the project (skip tests)
mvn clean package -Dmaven.test.skip=true

# Run the application
java -jar mc-admin/target/mc-admin.jar

# Or use batch scripts (Windows)
bin\clean.bat     # Clean target directories
bin\package.bat   # Build the project
bin\run.bat       # Run the JAR file
```

### Frontend - Web (Vue 3 + Vite)

```bash
cd mc-ui/MindCampus-Web
npm install
npm run dev          # Development server
npm run build:prod   # Production build
```

### Frontend - Mobile App (Uni-app)

Open `mc-ui/MindCampus-App` in HBuilderX IDE for development and building.

## Architecture

This is a multi-module Maven project following layered architecture:

```
mc-admin      → Application entry point, REST controllers, startup class (RuoYiApplication)
mc-project    → Core business modules (student, questionnaire, evaluation, recommend, counselor, banner)
mc-ai         → AI module using Spring AI Alibaba + DashScope for psychological analysis
mc-system     → System management (users, roles, menus, departments, posts, configs)
mc-framework  → Core framework (security, AOP, configs for Redis/Druid/MyBatis/Security)
mc-common     → Shared utilities, annotations, exceptions, constants, base entities
mc-quartz     → Scheduled task management (Quartz)
mc-generator  → Code generator from database tables
mc-ui         → Frontend projects (MindCampus-Web for admin, MindCampus-App for mobile)
```

### Module Dependencies

```
mc-admin
├── mc-framework → mc-system → mc-common
├── mc-project → mc-system, mc-common
├── mc-ai → mc-framework
├── mc-quartz → mc-common
└── mc-generator → mc-common
```

### Standard Package Structure (per business module in mc-project)

```
[module]/
├── controller/     # REST API endpoints
├── service/impl/   # Business logic implementation
├── mapper/         # MyBatis mappers
└── domain/         # Entities, DTOs, VOs
```

## Key Configuration Files

- `mc-admin/src/main/resources/application.yml` - Main Spring Boot config
- `mc-admin/src/main/resources/application-druid.yml` - Database connection pool config

## Database

MySQL 8.x with initialization scripts in `sql/`:
- `ry_20250522.sql` - Main database schema
- `quartz.sql` - Quartz scheduler tables

## API Documentation

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Knife4j UI: `http://localhost:8080/doc.html`

## Security & Auth

- JWT-based authentication with token storage in Redis
- RBAC permission model: User → Role → Menu/Permission
- Key classes: `TokenService`, `JwtAuthenticationTokenFilter`, `PermissionService`
- Security annotations: `@PreAuthorize("@ss.hasPermi('...')")`

## Response Conventions

```java
// Success
return AjaxResult.success();
return AjaxResult.success(data);

// Error
return AjaxResult.error("message");

// Pagination
return getDataTable(list);
```

## Common Annotations

- `@Log(title = "...", businessType = BusinessType.INSERT)` - Operation logging
- `@DataScope` - Data permission filtering
- `@RateLimiter` - API rate limiting
- `@RepeatSubmit` - Prevent duplicate submissions
- `@Excel` - Excel import/export field mapping

## AI Integration

The AI module (`mc-ai`) uses Spring AI Alibaba to integrate with DashScope (Tongyi Qianwen):
- `ChatController` - AI chat endpoints (sync and SSE streaming)
- `EvaluationAiService` - Psychological evaluation AI analysis
- Endpoints: `/chatTest`, `/chat/SSEStream`, `/chat/stream`
