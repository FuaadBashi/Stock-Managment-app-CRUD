package ws.aperture.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import ws.aperture.stock.enums.Storage;
import ws.aperture.stock.model.StockRecord;

public record StockRecordDTO(
    Long id,
    Long stockItemId,
    LocalDate incomingDate,
    LocalDate expireDate,
    BigDecimal quantity,
    Storage storage) {
  public static StockRecordDTO generateDTO(StockRecord r) {
    return new StockRecordDTO(
        r.getId(),
        r.getStockItem().getId(),
        r.getIncomingDate(),
        r.getExpiryDate(),
        r.getQuantity(),
        r.getStorage());
  }
}
