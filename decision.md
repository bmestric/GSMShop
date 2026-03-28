# GSM Shop - Decision Log

## Decision 001: Camera as a separate entity (2026-03-28)
**Context**: Originally `Phone` had a single `cameraSpec` string field (e.g. "200MP + 12MP + 50MP + 10MP").
**Decision**: Replace `cameraSpec` with a `Camera` entity linked via `@OneToMany` to `Phone`.
**Reason**: Modern phones have multiple cameras (Main, Telephoto, Ultrawide, Macro) each with their own megapixel count and aperture. A dedicated entity allows:
- Structured comparison in the phone comparison feature
- Better display of camera specs per lens type
- Easy extensibility (e.g. adding OIS, sensor size later)

**Camera fields**: `type` (Main, Ultrawide, Telephoto, Macro), `megapixels`, `aperture`, `phone` (ManyToOne)

## Decision 002: Minimal SecurityConfig in Phase 1 (2026-03-28)
**Context**: Phase 1 needed `PasswordEncoder` for the data seeder but full security was Phase 2.
**Decision**: Created a minimal `SecurityConfig` with just `PasswordEncoder` and permit-all in Phase 1, then replaced it with full dual filter chain config in Phase 2.
**Reason**: Allows incremental development without circular dependencies.

## Decision 003: ProductSeeder split from AppStartupListener (2026-03-28)
**Context**: Seeding admin + categories + products in one class would approach the 200-line limit.
**Decision**: Split product seeding into a separate `ProductSeeder` component called by `AppStartupListener`.
**Reason**: Keeps both classes well under 200 lines and follows single responsibility principle.

## Decision 004: ServletComponentScan removed for Spring Boot 4 (2026-03-28)
**Context**: `@ServletComponentScan` does not exist in Spring Boot 4.0.5.
**Decision**: Use `@Component` for listeners instead of `@WebListener` + `@ServletComponentScan`.
**Reason**: Spring Boot 4 removed this annotation. Spring-managed beans via `@Component` achieve the same result.

## Decision 005: Jackson 3.x package change in Spring Boot 4 (2026-03-28)
**Context**: Spring Boot 4.0.5 ships Jackson 3.1.0 which moved from `com.fasterxml.jackson` to `tools.jackson`.
**Decision**: Use `tools.jackson.databind.JsonNode` for PayPal REST API response parsing. Added `spring-boot-starter-json` dependency explicitly.
**Reason**: Jackson 3.x is a breaking change from 2.x. All Jackson imports must use the `tools.jackson` package prefix.
