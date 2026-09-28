package ws.aperture.stock.dto;

import java.time.LocalDate;
import ws.aperture.stock.enums.Storage;

/**
 * A delivery of stock.
 *
 * @param incomingDate defaults to today
 * @param storage defaults to {@link Storage#ROOM_TEMP}
 */
public record StockRecordRequestDTO(
        LocalDate incomingDate, LocalDate expiryDate, double quantity, Storage storage) {}
