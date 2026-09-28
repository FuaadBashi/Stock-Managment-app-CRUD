package ws.aperture.stock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequestDTO(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 2000) String desc,
    @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal retailPrice) {}
