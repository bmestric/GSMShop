# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

GSM Shop — online mobile phone and accessories web shop. College project for JavaWebProgramiranje course.

- **Stack**: Spring Boot 4.0.5, Java 21, H2 (in-memory), Thymeleaf, Spring Security, JWT, Lombok
- **Base package**: `hr.bmestric.gsmshop`
- **Build**: Maven (wrapper included)

## Build & Run Commands

```bash
./mvnw spring-boot:run          # Run the application
./mvnw compile                  # Compile only
./mvnw test                     # Run all tests
./mvnw test -Dtest=ClassName    # Run a single test class
./mvnw test -Dtest=ClassName#methodName  # Run a single test method
./mvnw clean package            # Build JAR
```

H2 console available at `/h2-console` when running (JDBC URL: `jdbc:h2:file:./data/gsmshopdb`, user: `sa`, password: `ThisIsTest1!`). Database is file-based and persists across restarts.

## Architecture

### Dual Security Model
Two `SecurityFilterChain` beans: session-based form login for web UI (`/**`) and stateless JWT bearer auth for REST API (`/api/**`). CSRF enabled for web, disabled for API.

### Product Inheritance
`Product` uses JPA SINGLE_TABLE inheritance with discriminator column `product_type`. `Phone` (with hardware specs) and `Accessory` extend it. All share one DB table.

### Session-Based Cart
Shopping cart is stored in `HttpSession` (not database) to support anonymous users. Cart is a serializable DTO, not a JPA entity.

### Package Layout
- `controller/web/` — Thymeleaf MVC controllers
- `controller/rest/` — JWT-secured REST API controllers
- `service/` interfaces, `service/impl/` implementations
- `security/` — JwtUtil, JwtAuthFilter, CustomUserDetailsService
- `filter/` — servlet filters (RequestLoggingFilter)
- `listener/` — servlet/app listeners (SessionListener, AppStartupListener)

## Constraints

- **No class may exceed 200 lines of code** — split into smaller classes if approaching the limit
- **SonarQube must report zero errors**
- Must include at least one servlet Filter and one Listener
- Must use `@Async` for at least one operation
- PayPal integration uses sandbox REST API via Spring `RestClient` (not PayPal SDK)


## General Instructions
- Visual design should be clean and responsive
- always adhere to best practices for Spring Boot applications (layered architecture, dependency injection, exception handling, etc.)
- Ensure proper authentication and authorization for all endpoints
- follow SOLID and clean code principles to maintain readability and maintainability
- while implementing, always follow the phased build order outlined in `plan.md` to ensure a structured development process and avoid unnecessary refactoring later on.
- Track decisions and store them in decision.md so we can refer back to them if needed.
- if there is any uncertainty or ambiguity in the requirements, ask for clarification and document the clarification in decision.md for future reference.
- create a document that will track progress of our plan execution, so we can easily see what has been done and what is left to do. This will help us stay organized and ensure that we are following the plan effectively.
## GitHub
- Create a new private Git repository for this project. 
- Always have meaningfull commit messages, save them locally and then push to github.
- Make commits and pushs in order of development so we can track changes and have possibility to revert back.

## Spring Boot 4 Gotchas
- **No `@ServletComponentScan`** — use `@Component` for servlet listeners instead
- **Jackson 3.x** — package is `tools.jackson` not `com.fasterxml.jackson`. Need `spring-boot-starter-json` dep explicitly.
- **Hibernate proxies** — never use `getClass().getSimpleName()` for type checks in templates. Use `@DiscriminatorValue`-based helper methods (`isPhone()`, `isAccessory()` on `Product`).

## Reference

- `plan.md` — full implementation plan with phased build order (Phases 1-6.5 complete, 7-9 remaining)
- `decision.md` — architectural decisions log (11 decisions recorded)
- `urls.md` — local development URLs (H2 console, app pages, admin)
