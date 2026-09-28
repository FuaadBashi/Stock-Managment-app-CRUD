package ws.aperture.stock.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CustomerOrderItemDTO(
    @NotNull @Positive Long productId, @Min(1) @Max(100000) int quantity) {}
