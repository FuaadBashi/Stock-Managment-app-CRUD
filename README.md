# Stock Management API

[![Build and test](https://github.com/FuaadBashi/Stock-Managment-app-CRUD/actions/workflows/ci.yml/badge.svg)](https://github.com/FuaadBashi/Stock-Managment-app-CRUD/actions/workflows/ci.yml)

A runnable Java 21 / Spring Boot API for supplier records, stock receipts, product recipes, staff records, and customer orders. Built with Spring MVC, Spring Data JPA, Spring Security, and Flyway; runs locally with a persistent H2 database or with PostgreSQL.

## Run it

Install **JDK 21**. Maven is downloaded by the included wrapper on first use.

```sh
./mvnw verify
export APP_PASSWORD="$(openssl rand -hex 24)"
./mvnw spring-boot:run
```

Windows: use `mvnw.cmd` and set `APP_PASSWORD` in your shell. The default username is `admin`; `APP_USERNAME` overrides it. Without `APP_PASSWORD`, Spring Security generates a temporary password in the startup log.

The server binds to `127.0.0.1:8080`. Local records survive restarts in `data/`. The health endpoint is public; every business endpoint requires HTTP Basic authentication. Mutations also require a CSRF token and its session cookie.

In another terminal, set the **same** `APP_PASSWORD`, then run:

```sh
python3 scripts/demo.py
```

The demo creates a supplier, stock receipt, ingredient, product recipe, staff record, and an order; it starts and completes the order and checks its total. It deliberately leaves those records available for inspection. Repeated runs use unique supplier and staff identifiers. `APP_URL` selects a different server.

To package and run independently of Maven:

```sh
./mvnw package
java -jar target/stock-management-1.0.0.jar
```

## API

All request and response bodies are JSON. The [demo](scripts/demo.py) shows authentication, session cookies, CSRF handling, and exact request bodies.

| Resource | Routes |
| --- | --- |
| Staff records | `GET /users`, `POST /users`, `GET/PUT/DELETE /users/{id}` |
| Suppliers | `GET /suppliers`, `POST /suppliers`, `GET/PUT/DELETE /suppliers/{id}` |
| Ingredients | `GET /ingredients`, `POST /ingredients`, `GET/PUT/DELETE /ingredients/{id}` |
| Products | `GET /products`, `POST /products`, `GET/PUT/DELETE /products/{id}` |
| Product recipes | `GET/PUT /products/{id}/recipe` |
| Stock catalogue | `GET /stock`, `POST /stock`, `GET/DELETE /stock/{id}`, `PUT /stock/{id}/details` |
| Stock receipts | `GET/POST /stock/{id}/receipts` |
| Orders | `GET /customer-orders`, `POST /customer-orders`, `GET/PUT/DELETE /customer-orders/{id}` |
| Order actions | `POST /customer-orders/{id}/start`, `/complete`, `/cancel` |
| CSRF token | `GET /csrf` |
| Health | `GET /actuator/health` |

Creation returns `201`; invalid input returns `400`, missing records `404`, and conflicting changes `409`. Application errors use Spring's Problem Detail response format; security failures return `401` or `403`. Unknown JSON fields are rejected, so clients cannot set internal IDs, roles, or order statuses through request bodies. Legacy route aliases remain available in the controllers.

### Rules enforced by the code

- Prices use `BigDecimal` with two decimal places. Recipe and receipt quantities support three decimal places. Prices and quantities must be positive.
- Recipes are replaced atomically. Duplicate ingredients, missing ingredients, and invalid quantities leave the previous recipe intact.
- Orders store the unit price at creation or edit time. Later product price changes do not rewrite existing orders. Only `NEW` orders can be edited.
- Orders move from `NEW` to `STARTED` to `COMPLETED`. New/started orders can be cancelled; new/cancelled orders can be soft-deleted. Repeating an action is idempotent. Competing transitions are serialized by database locks.
- Deleting a product discontinues it and prevents new orders for it. Existing order lines remain readable.
- Suppliers, staff, ingredients, and stock items cannot be deleted while referenced by protected history or recipes. Receipt expiry cannot precede arrival.
- Email and company-number uniqueness is normalized. Supplier names also have a normalized database uniqueness constraint.

## PostgreSQL

Create an empty database and a database user with permission to create tables, then set:

```sh
export SPRING_PROFILES_ACTIVE=postgres
export DATABASE_URL=jdbc:postgresql://localhost:5432/stock
export DATABASE_USER=stock
export DATABASE_PASSWORD='your-database-password'
export APP_PASSWORD='your-application-password'
./mvnw spring-boot:run
```

Flyway creates the schema and Hibernate validates it on startup. The initial migration targets a **new** database; an older external database needs a separate migration plan. Database connection settings do not migrate existing H2 records to PostgreSQL.

## Verification and code map

`./mvnw spotless:apply` formats Java sources; `verify` checks formatting.

`./mvnw verify` runs integration scenarios through the real HTTP controller/security stack, service transactions, migrations, and repositories. Tests cover validation, CRUD, duplicate records, recipe replacement, order snapshots and rollback, receipt persistence, history protection, and competing order transitions. GitHub Actions runs the suite against both H2 and PostgreSQL 17.

To run the same suite against a dedicated PostgreSQL test database:

```sh
./mvnw verify \
  -Dspring.datasource.url=jdbc:postgresql://localhost:5432/stock_test \
  -Dspring.datasource.username=stock \
  -Dspring.datasource.password=test-password
```

Tests create records; use a disposable test database.

- [Controllers](src/main/java/ws/aperture/stock/controller): routes, request validation, response codes.
- [Services](src/main/java/ws/aperture/stock/service): business rules and transaction boundaries.
- [Models](src/main/java/ws/aperture/stock/model) and [migration](src/main/resources/db/migration/V1__initial_schema.sql): relational integrity and exact numeric types.
- [Integration tests](src/test/java/ws/aperture/stock/ApiIntegrationTest.java): executable workflow examples.
- [Legacy route tests](src/test/java/ws/aperture/stock/LegacyRoutesAndLookupsTest.java): every legacy alias and a not-found response for each resource.

## Scope

This is a single-operator backend portfolio project. Staff records are business data, not login accounts; authentication uses one configured operator account. Stock receipts record incoming inventory; order completion does not deduct ingredients or calculate stock on hand. There is no payment processing, frontend, multi-tenant authorization, or paginated reporting yet.

For an external deployment, terminate HTTPS at a reverse proxy, configure credentials and database backups, and explicitly set `SERVER_ADDRESS=0.0.0.0`. The local defaults are intended for evaluation on your own machine.
