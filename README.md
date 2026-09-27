# Stock Management — Spring Boot Source

A Java/Spring stock-management project with controllers, services, repositories, DTOs, and domain exceptions for products, customer orders, suppliers, and inventory.

## Code to explore

| Area | Entry point |
| --- | --- |
| Application | [StockApp.java](StockApp.java) |
| Products | [ProductController.java](ProductController.java), [ProductService.java](ProductService.java) |
| Orders | [CustomerOrderController.java](CustomerOrderController.java), [CustomerOrderService.java](CustomerOrderService.java) |
| Inventory | [StockController.java](StockController.java), [StockService.java](StockService.java) |
| Error responses | [ControllerExceptionHandler.java](ControllerExceptionHandler.java) |

Follow a request from controller to service to repository to see the layering and validation decisions.

## Repository status

This checkout contains Java source files at the repository root. It does **not** include a Maven/Gradle build definition or application configuration, so it is currently a source-code showcase rather than a standalone runnable service.

To make it reproducible, the next step is to restore the original dependency versions, package-directory layout, database configuration, and integration tests. No build command is supplied here because those pieces are not present.
