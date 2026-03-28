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

## Decision 006: Product type checks via @DiscriminatorValue instead of getClass() (2026-03-28)
**Context**: Hibernate creates proxy subclasses at runtime (e.g. `Phone$HibernateProxy`), so `getClass().getSimpleName()` never returns `"Phone"`, causing 500 errors in Thymeleaf templates.
**Decision**: Added `isPhone()` and `isAccessory()` helper methods to `Product` that read the `@DiscriminatorValue` annotation from the class. All templates now use `product.isPhone()` / `product.isAccessory()`.
**Reason**: Proxy-safe approach that works with JPA SINGLE_TABLE inheritance regardless of lazy loading or caching.

## Decision 008: Transactional admin product save (2026-03-28)
**Context**: Editing a product with cameras caused a 500 error. The `findById` and `save` calls ran in separate transactions, so the Phone entity was detached when cameras were modified via `clear()+addAll()`.
**Decision**: Added `@Transactional` on `AdminProductController.save()` so that find + modify + save runs in one transaction. The entity stays managed, and orphanRemoval on cameras works correctly.
**Reason**: JPA orphanRemoval requires the entity to be managed (attached to persistence context) when the collection is modified.

## Decision 009: Cart stock validation and redirect-back (2026-03-28)
**Context**: Users could add out-of-stock products to cart, causing negative stock. Also, adding to cart redirected to `/cart`, forcing users to navigate back.
**Decision**: Added stock validation in `CartServiceImpl.addToCart()` (checks current cart qty + requested vs available stock). Changed `CartController.addToCart` to redirect back to the Referer URL with flash messages. Added global Bootstrap toast notifications in the footer fragment.
**Reason**: Prevents overselling and improves UX by keeping users on their current browsing page.

## Decision 010: Anonymous cart access per project requirements (2026-03-28)
**Context**: Cart endpoints were restricted to authenticated users (`/cart/**` → `.authenticated()`), but `projectRequirements.md` line 19 states anonymous users can add products to cart.
**Decision**: Moved `/cart/**` to `.permitAll()`. Removed `sec:authorize="isAuthenticated()"` from all add-to-cart forms. Only `/checkout/**` and `/order-history/**` remain authenticated. CSRF exempted for `/h2-console/**` to fix 403 on H2 console.
**Reason**: Requirements mandate anonymous cart support. Purchase completion (checkout) is the auth boundary, not the cart.

## Decision 011: File-based H2 with ddl-auto=update (2026-03-28)
**Context**: In-memory H2 (`jdbc:h2:mem:`) with `create-drop` lost all data on every restart — users, products, orders were wiped.
**Decision**: Switched to file-based H2 (`jdbc:h2:file:./data/gsmshopdb`) with `ddl-auto=update`. Added `data/` to `.gitignore`.
**Reason**: Persistent storage needed for development/testing. `update` mode preserves existing data while applying schema changes.

## Decision 007: LoginSuccessHandler constructor for default URL (2026-03-28)
**Context**: `defaultSuccessUrl("/", true)` in SecurityConfig conflicted with custom `successHandler()`. Login history was not being recorded.
**Decision**: Removed `defaultSuccessUrl` from SecurityConfig. Set `setDefaultTargetUrl("/")` and `setAlwaysUseDefaultTargetUrl(true)` in `LoginSuccessHandler`'s constructor instead.
**Reason**: When both are configured, the handler set by `defaultSuccessUrl` can override the custom one. Setting it on the handler itself ensures login history recording fires before redirect.
