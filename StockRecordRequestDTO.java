package ws.aperture.stock.dto;

import java.time.LocalDate;

public record StockRecordRequestDTO( Long stockItemId, LocalDate incomingDate, LocalDate expiryDate, double quantity ) {}
