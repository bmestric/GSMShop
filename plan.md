# GSM Shop - Implementation Plan

## Context
Online GSM (mobile phone + accessories) shop for JavaWebProgramiranje college course. Built with Spring Boot 4.0.5, Java 21, H2 database, Thymeleaf, Spring Security, JWT, PayPal sandbox integration. Additional feature: phone specification comparison.

---

## Architecture

### Package Structure (`hr.bmestric.gsmshop`)
```
config/          SecurityConfig, AsyncConfig, WebMvcConfig
entity/          JPA entities
enums/           Role, PaymentMethod, ProductType
repository/      Spring Data JPA repositories
service/         Service interfaces
service/impl/    Service implementations
controller/web/  Thymeleaf MVC controllers
controller/rest/ JWT-secured REST API controllers
dto/             Request/response DTOs, Cart/CartItem
security/        JwtUtil, JwtAuthFilter, CustomUserDetailsService
filter/          RequestLoggingFilter
listener/        SessionListener, AppStartupListener
mapper/          Entity-DTO mappers
exception/       GlobalExceptionHandler
```

### Database Model

```
Category  1---*  Product (SINGLE_TABLE inheritance)
                   ├── Phone     (screenResolution, screenSize, cameraSpec,
                   │              batteryCapacity, chargingPower, ramGb, romGb, processor)
                   └── Accessory (accessoryType, compatibleModels)

AppUser  1---*  Order  1---*  OrderItem  *---1  Product
AppUser  1---*  LoginHistory
```

**Cart** is session-based (HttpSession), not DB-persisted — supports anonymous users.

### Entity Details

**BaseEntity** (MappedSuperclass): `id`, `createdAt`, `updatedAt`

**Category**: `name`, `description`, `imageUrl`, `products` (OneToMany)

**Product** (abstract, SINGLE_TABLE): `name`, `description`, `price`, `imageUrl`, `stockQuantity`, `category` (ManyToOne)

**Phone** (extends Product, discriminator="PHONE"): `screenResolution`, `screenSize`, `cameras` (OneToMany → Camera), `batteryCapacity`, `chargingPower`, `ramGb`, `romGb`, `processor`

**Camera**: `type` (Main, Ultrawide, Telephoto, Macro), `megapixels`, `aperture`, `phone` (ManyToOne → Phone)

**Accessory** (extends Product, discriminator="ACCESSORY"): `accessoryType`, `compatibleModels`

**AppUser**: `firstName`, `lastName`, `email` (unique), `password` (BCrypt), `role` (USER/ADMIN)

**Order** (table="customer_order"): `user`, `orderDate`, `paymentMethod` (CASH_ON_DELIVERY/PAYPAL), `paypalTransactionId`, `totalAmount`, `items` (OneToMany)

**OrderItem**: `order`, `product`, `quantity`, `priceAtPurchase`

**LoginHistory**: `user`, `loginTime`, `ipAddress`, `userAgent`

### Security: Dual Filter Chains

| Zone | Path | Auth | Session | CSRF |
|------|------|------|---------|------|
| Web | `/**` | Form login | Stateful | Enabled |
| REST API | `/api/**` | JWT Bearer | Stateless | Disabled |

**JWT**: Access token (15 min) + Refresh token (7 days), using jjwt library.

**Login history**: Recorded via custom `AuthenticationSuccessHandler`.

---

## Controllers & Endpoints

### Web Controllers (`controller/web/`)

| Controller | Key Endpoints |
|---|---|
| HomeController | `GET /` |
| CategoryController | `GET /categories`, `GET /categories/{id}` |
| ProductController | `GET /products`, `GET /products/{id}` |
| PhoneCompareController | `GET /compare?ids=1,2,3` |
| CartController | `GET /cart`, `POST /cart/add`, `POST /cart/update`, `POST /cart/remove/{id}`, `POST /cart/clear` |
| CheckoutController | `GET /checkout`, `POST /checkout/cod`, `POST /checkout/paypal/create`, `GET /checkout/paypal/success`, `GET /checkout/paypal/cancel` |
| OrderHistoryController | `GET /order-history`, `GET /order-history/{id}` |
| AuthController | `GET /login`, `GET /register`, `POST /register` |
| AdminCategoryController | CRUD at `/admin/categories/**` |
| AdminProductController | CRUD at `/admin/products/**` |
| AdminOrderController | `GET /admin/orders` (filters: customerId, dateFrom, dateTo) |
| AdminLoginHistoryController | `GET /admin/login-history` |

### REST API Controllers (`controller/rest/`)

| Controller | Key Endpoints |
|---|---|
| AuthRestController | `POST /api/auth/login`, `POST /api/auth/refresh`, `POST /api/auth/register` |
| ProductRestController | `GET /api/products`, `GET /api/products/{id}` |
| CartRestController | `POST /api/cart/add`, `PUT /api/cart/{id}`, `DELETE /api/cart/{id}`, `DELETE /api/cart` |
| OrderRestController | `GET /api/orders`, `POST /api/orders` |

---

## Templates (`src/main/resources/templates/`)

```
fragments/header.html, footer.html, head.html
index.html
auth/login.html, register.html
category/list.html, products.html
product/list.html, detail.html
cart/view.html
checkout/form.html, success.html, cancel.html
compare/compare.html
order/history.html, detail.html
admin/dashboard.html
admin/category/list.html, form.html
admin/product/list.html, form.html
admin/order/list.html, detail.html
admin/login-history.html
error/404.html, 403.html
```

**UI**: Bootstrap 5.3 via CDN. Custom CSS in `static/css/custom.css`.

---

## Phone Comparison Feature

1. Product listing shows "Compare" checkbox on phone cards
2. JavaScript (`static/js/compare.js`) stores selected IDs in localStorage (max 4)
3. Floating "Compare (N)" button appears when 2+ selected
4. `GET /compare?ids=1,5,12` shows side-by-side table
5. Spec rows: Screen Resolution, Screen Size, Camera, Battery, Charging Power, RAM, ROM, Processor, Price
6. Best values highlighted with CSS class

---

## Required Implementations

### Filter: `RequestLoggingFilter`
- Logs: timestamp, HTTP method, URI, remote IP, response status, elapsed time
- Registered via `@Component` + `@Order(1)`

### Listener: `SessionListener`
- `HttpSessionListener` via `@WebListener`
- Tracks active session count (AtomicInteger counter)

### Listener: `AppStartupListener`
- Seeds default admin (admin@gsmshop.hr), sample categories, sample products on startup

### Async
- `@EnableAsync` with `ThreadPoolTaskExecutor` (core=2, max=5)
- `@Async` on: order confirmation email (console log), login history recording

### PayPal Integration
- PayPal Orders v2 REST API via Spring `RestClient`
- Sandbox mode, credentials in `application.properties`
- Flow: Create order -> Redirect to PayPal -> Capture on return

---

## Implementation Phases

### Phase 1: Foundation ✅ (completed 2026-03-28)
- [x] Update `pom.xml` (jjwt deps, validation)
- [x] Create enums, BaseEntity, all JPA entities
- [x] Create all repositories
- [x] Configure `application.properties`
- [x] `AppStartupListener` + `ProductSeeder` (data seeder)
- [x] Verify tables in H2 console
- [x] **Added**: `Camera` entity (replaces `cameraSpec` string — see decision.md #001)

### Phase 2: Security ✅ (completed 2026-03-28)
- [x] `CustomUserDetailsService`
- [x] `SecurityConfig` (dual filter chains)
- [x] `JwtUtil`, `JwtAuthenticationFilter`
- [x] `AuthController` + login/register templates
- [x] `LoginSuccessHandler` (login history)
- [x] Thymeleaf fragments (navbar, head, footer) + custom.css

### Phase 3: Public Catalog ✅ (completed 2026-03-28)
- [x] `CategoryService`, `ProductService` (interfaces + impls)
- [x] `HomeController`, `CategoryController`, `ProductController`
- [x] Templates: index, category/list, category/products, product/list, product/detail
- [x] Bootstrap navbar fragments (done in Phase 2)
- [x] Product detail shows Phone specs + cameras table, Accessory details
- [x] Category filter pills on product list, hover animations on cards

### Phase 4: Shopping Cart ✅ (completed 2026-03-28)
- [x] `Cart`/`CartItem` DTOs (serializable, session-based)
- [x] `CartService` (interface + impl), `CartController`
- [x] `cart/view.html` (table with qty controls, order summary)
- [x] Cart badge in navbar via `CartModelAdvice` (@ControllerAdvice)

### Phase 5: Checkout & Orders
- [ ] `OrderService`, `CheckoutController`
- [ ] Cash on Delivery flow
- [ ] `PayPalService` (sandbox)
- [ ] `OrderHistoryController` + templates

### Phase 6: Admin Panel
- [ ] Category CRUD (controller + templates)
- [ ] Product CRUD (controller + templates, conditional phone fields)
- [ ] Order list with filters
- [ ] Login history view

### Phase 7: Comparison, Filter, Listener, Async
- [ ] `PhoneCompareController` + `compare.html`
- [ ] `compare.js` (phone selection UI)
- [ ] `RequestLoggingFilter`
- [ ] `SessionListener`
- [ ] `AsyncConfig` + `@Async` methods

### Phase 8: REST API
- [ ] `AuthRestController` (JWT login/refresh)
- [ ] `ProductRestController`, `CartRestController`, `OrderRestController`

### Phase 9: Polish & Quality
- [ ] `GlobalExceptionHandler`, error pages
- [ ] Input validation (`@Valid`)
- [ ] 200-line class limit review
- [ ] SonarQube scan — zero errors

---

## Key Dependencies to Add to `pom.xml`

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

## Key `application.properties`

```properties
spring.application.name=gsmShop

# H2
spring.datasource.url=jdbc:h2:mem:gsmshopdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Thymeleaf
spring.thymeleaf.cache=false

# JWT
app.jwt.secret=your-256-bit-secret-key-here
app.jwt.access-token-expiration=900000
app.jwt.refresh-token-expiration=604800000

# PayPal Sandbox
paypal.client-id=YOUR_SANDBOX_CLIENT_ID
paypal.client-secret=YOUR_SANDBOX_CLIENT_SECRET
paypal.base-url=https://api-m.sandbox.paypal.com
```
