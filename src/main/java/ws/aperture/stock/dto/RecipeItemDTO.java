package ws.aperture.stock.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record RecipeItemDTO(
    @NotNull @Positive Long ingredientId,
    @NotNull @DecimalMin("0.001") @Digits(integer = 11, fraction = 3) BigDecimal quantity) {}
