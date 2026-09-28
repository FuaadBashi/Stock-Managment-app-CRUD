package ws.aperture.stock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import ws.aperture.stock.enums.Storage;

public record StockRecordRequestDTO(
    @Positive Long stockItemId,
    @NotNull LocalDate incomingDate,
    @NotNull LocalDate expiryDate,
    @NotNull @DecimalMin("0.001") @Digits(integer = 11, fraction = 3) BigDecimal quantity,
    Storage storage) {}
