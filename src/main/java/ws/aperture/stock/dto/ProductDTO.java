package ws.aperture.stock.dto;

import java.math.BigDecimal;
import ws.aperture.stock.enums.ProductAndStockStatus;
import ws.aperture.stock.model.Product;

public record ProductDTO(
    Long id,
    String productName,
    BigDecimal retailPrice,
    String description,
    ProductAndStockStatus status) {
  public static ProductDTO generateDTO(Product p) {
    return new ProductDTO(
        p.getId(), p.getName(), p.getRetailPrice(), p.getDescription(), p.getStatus());
  }
}
