package ws.aperture.stock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record StockItemRequestDTO(
    @NotBlank @Size(max = 100) String name,
    @NotNull @Positive Long supplierId,
    @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal retailPrice,
    @Size(max = 2000) String desc) {}
