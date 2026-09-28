package ws.aperture.stock.dto;

import java.math.BigDecimal;
import ws.aperture.stock.model.StockItem;

public record StockItemDTO(
    Long stockItemId, String name, Long supplierId, BigDecimal retailPrice, String desc) {
  public static StockItemDTO generateDTO(StockItem s) {
    return new StockItemDTO(
        s.getId(), s.getName(), s.getSupplier().getId(), s.getRetailPrice(), s.getDescription());
  }
}
