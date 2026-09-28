# Stock Management API

[![CI](https://github.com/FuaadBashi/Stock-Managment-app-CRUD/actions/workflows/ci.yml/badge.svg)](https://github.com/FuaadBashi/Stock-Managment-app-CRUD/actions/workflows/ci.yml)

A Spring Boot REST API for running a café's stock: suppliers, stock items and deliveries,
products with ingredient recipes, users, and customer orders that move through a lifecycle.

## Highlights

- **Layered architecture.** Controllers validate requests and map them to DTOs. Services hold the
  business rules and transactions. Spring Data JPA repositories handle persistence.
- **Relational model.** Composite keys (`@IdClass`) for recipe lines and order lines, many-to-one
  and one-to-many associations, and enums stored as strings.
- **Consistent errors.** A `@RestControllerAdvice` maps each domain exception to a status code: 404
  for anything not found, 400 for invalid input.
- **Immutable DTOs.** Every response is a Java `record`, so entities never leak out of the API.
- **Runs with zero setup.** An in-memory H2 database is configured by default; swap the datasource
  URL for PostgreSQL or MySQL to persist data.
- **Integration tests.** `@SpringBootTest` + MockMvc tests drive the real HTTP layer and database.

## Getting started

Requires JDK 17+ and Maven.

```bash
git clone https://github.com/FuaadBashi/Stock-Managment-app-CRUD.git
cd Stock-Managment-app-CRUD
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. While it runs, the database console is at
`/h2-console` (JDBC URL `jdbc:h2:mem:stock`, user `sa`, no password).

```bash
# Register a supplier, add a stock item, record a delivery
curl -X PUT localhost:8080/suppliers/register -H 'Content-Type: application/json' \
     -d '{"name": "Fresh Farms Ltd", "companyNumber": "12345678"}'
curl -X POST localhost:8080/stock -H 'Content-Type: application/json' \
     -d '{"name": "Whole milk", "supplierId": 1, "retailPrice": 1.10, "desc": "1 litre"}'
curl -X PUT localhost:8080/stock/1 -H 'Content-Type: application/json' \
     -d '{"expiryDate": "2030-01-31", "quantity": 12, "storage": "CHILLED"}'
```

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `PUT` | `/suppliers/register` | Register a supplier (name > 6 chars, 8-digit company number) |
| `GET` | `/suppliers/all`, `/suppliers/{id}` | List or fetch suppliers |
| `DELETE` | `/suppliers/remove/{id}` | Remove a supplier |
| `POST` | `/stock` | Add a stock item for a supplier |
| `PUT` | `/stock/{id}` | Record a delivery: quantity, expiry, optional incoming date and storage (defaults to today and `ROOM_TEMP`) |
| `GET` | `/stock` | List stock items |
| `PUT` | `/products` | Create a product (name and positive price required) |
| `POST` | `/products/update-recipe/{productId}` | Set a product's recipe from ingredient quantities |
| `GET` | `/products`, `/ingredients` | List products or ingredients |
| `POST` | `/users` | Register a user |
| `GET` / `DELETE` | `/users/{userId}` | Fetch or remove a user |
| `POST` | `/customer-orders/new` | Place an order for one or more products |
| `POST` | `/customer-orders/{start,complete,cancel,delete}/{id}` | Move an order through its lifecycle |
| `GET` | `/customer-orders/{id}` | Fetch an order, its lines and status |

## Project structure

```
src/main/java/ws/aperture/stock/
├── StockApp.java     Spring Boot entry point
├── controller/       REST endpoints and exception mapping
├── service/          business rules and transactions
├── repository/       Spring Data JPA interfaces
├── model/            JPA entities; keys/ holds composite primary keys
├── dto/              request and response records
├── enums/            order status, units, storage, roles
└── exceptions/       domain exceptions
```

## Tests

```bash
mvn verify
```

This runs the integration tests and checks formatting with google-java-format.
