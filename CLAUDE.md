# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

- Build: `mvn clean install` — no Maven wrapper (mvnw was deleted); requires system Maven 3.6+ and Java 17
- Run: `mvn spring-boot:run` — serves on http://localhost:8080, Swagger UI at /swagger-ui.html
- All tests: `mvn test`
- Single test: `mvn test -Dtest=ClassName`
- Coverage: `mvn verify` — JaCoCo report in `target/site/jacoco/`
- Note: the test suite is currently placeholders only (`src/test/java/A.java`, `B.java`, one empty context-load test)

### Runtime prerequisites

- MySQL 8 on localhost:3306, database `rentACarBackend` (credentials in `application.properties`, root/root by default)
- `spring.jpa.hibernate.ddl-auto=create` plus `spring.sql.init.mode=always`: the schema is dropped/recreated and re-seeded from `src/main/resources/data.sql` on every boot — nothing in the DB survives a restart
- `pom.xml` pulls in spring-data-redis and resilience4j-ratelimiter; CORS is configured for a frontend at http://localhost:5173

## Architecture

Spring Boot 3.2.3 / Java 17. Root package `in.lakshay.rentACarBackend` (artifact `rentACarBackend`). Strict layered architecture; a request flows:

`api/controllers` → `business/abstracts` (service interfaces) → `business/concretes` (`*Manager` implementations) → `dataAccess/abstracts` (`*Dao` extends JpaRepository) → `entities`

### Conventions the whole codebase follows

- Every service method returns `Result` or `DataResult<T>` (`core/utilities/result`) — the `{success, message, data}` envelope all API responses use. Controllers return these wrappers directly.
- Naming is mechanical: interface `XService` (business/abstracts) ↔ impl `XManager` (business/concretes) ↔ `XDao` (dataAccess/abstracts). DTOs in `business/dtos` (`XListDto`, `GetXDto`), request models in `business/requests` (`CreateXRequest`/`UpdateXRequest`/`DeleteXRequest`).
- Business rules live inside Managers as private `checkIf*`/`checkAll*` methods that throw domain exceptions from `core/utilities/exceptions/businessExceptions` (all extend `BusinessException`); `GlobalExceptionHandler` maps them to the error envelope. Rental price math (total-day price, different-city delivery surcharge, per-day additional-service pricing) is computed server-side in `RentalCarManager`/`PaymentManager` — never trust client-sent amounts.
- Entity↔DTO mapping goes through `ModelMapperService` (`core/utilities/mapping`).

### Domain

Vehicle rental: `Car`/`Brand`/`Color`/`City`, `Customer` with `IndividualCustomer`/`CorporateCustomer` subtypes, `RentalCar` (booking), `Additional`/`OrderedAdditional` (add-on services), `Payment`/`CreditCard`/`Invoice`, `CarMaintenance`/`CarCrash`. Payments have separate endpoints per customer type (`makePaymentForIndividualRentAdd`, `makePaymentForCorporateRentAdd`, `makePaymentForOrderedAdditionalAdd`).

### Payment gateway adapters

`PaymentManager` calls the `PosService` abstraction; `business/adapters/posAdapters` contains `AkbankPosAdapter`/`ZiraatBankPosAdapter` wrapping the simulated bank services in `business/outServices`. Beware near-duplicate legacy packages: `core/posServices`, `core/postServices`, `core/outServices`, and `core/iptal` ("iptal" = cancelled) contain older copies of the same POS idea — the live path is the one under `business/`. There are also two exception packages: `core/utilities/exception` (the `BusinessException` base) and `core/utilities/exceptions` (handler + concrete exceptions).

### Security

JWT auth (jjwt 0.12): `config/SecurityConfig` + the `security/` package (`TokenProvider`, `TokenAuthenticationFilter`, `CustomUserDetailsService`, `UserPrincipal`). Token secret/expiry read from `app.auth.*` in `application.properties` via `AppProperties`. Stateless sessions, BCrypt passwords, role-based access (user/admin) with method-level `@PreAuthorize`. Public entry points: `POST /api/auth/login`, `POST /api/auth/signup`; catalog reads (cars/brands/colors/cities) are public, most writes need auth, admin-only lists per README tables.

### Docs

`docs/frontend-guide/` holds per-domain API contracts (car, brand, color, city, customer, user, rental, payment, additional) written for frontend integration — authoritative for endpoint shapes and the rental checkout flow (availability → rental → additionals → payment).
