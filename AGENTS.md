# AGENTS.md

Spring Boot 4 microservices split from the monolith in `../ecom-springboot` (reference only; make changes here).

## Build, test, run
- Use the root wrapper (`./mvnw`, Maven 3.9.16). Java target is 21; Spring Boot 4.1.1.
- `./mvnw clean verify` builds and tests all modules. There is no CI, linter, or formatter; this is the gate.
- One module: `./mvnw -pl order test`. One test: `./mvnw -pl order -Dtest=OrderApplicationTests test`.
- Modules have no Maven dependency on each other, so `-pl <module>` works without `-am`.
- Each module is its own Spring Boot app: `./mvnw -pl product spring-boot:run` or `java -jar product/target/product-0.0.1-SNAPSHOT.jar`.
- Tests are context-load smoke tests only (`*ApplicationTests`); there are no unit or integration tests.

## Layout and wiring
- Modules: `product` (:8081, `/api/products`), `user` (:8082, `/api/users`), `order` (:8083, `/api/orders` and `/api/cart`).
- Cart lives inside `order` in the same H2 database and transaction. Do not add a separate cart service or an order-to-cart HTTP call.
- Only cross-service call: order calls product (`product.service.url`, `RestClient`) to fetch price/stock when adding to cart. No gateway, service discovery, auth, or message broker.
- Add new modules to the root `pom.xml` `<modules>` and give each the `spring-boot-starter-parent` 4.1.1 parent, matching existing modules.
- H2 is in-memory with `ddl-auto: create`: data is empty and resets every restart, and nothing is seeded. Create a user/product before exercising cart or order.

## Contracts and gotchas
- All JSON DTOs use `@JsonNaming(SnakeCaseStrategy)`; fields are snake_case (`product_id`, `total_amount`).
- Spring Boot 4 uses Jackson 3: import `tools.jackson.databind.*`, never `com.fasterxml.jackson.*`.
- `/api/cart` and `/api/orders` require an `X-User-ID` header parsed as `Long`. The ID is trusted; order does not verify it against user-service.
- Cart stores unit price and validates only the newly requested quantity against product stock. Neither cart nor order decrements stock, and cumulative cart quantity is not re-validated.
- Cart `price` is per unit, while `OrderItem.price`/`sub_total` and order totals are line totals (`unit price × quantity`).
- Product deletion is soft (`is_active=false`); `GET /api/products/{id}` still returns inactive products and cart does not check `is_active`.
- Errors are handled per module in `GlobalExceptionHandler` and return `{"error": "..."}` (validation: `{"errors": {...}}`). Add new exceptions there.

## Repo conventions
- `.clinerules` requires prefixing shell commands with `rtk` (installed at `~/.local/bin/rtk`).
- Package root is `com.ecommerce.<module>` with controller/service/repository/model/dto/exception layers.
