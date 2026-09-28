package ws.aperture.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ws.aperture.stock.enums.Unit;

public record IngredientRequestDTO(@NotBlank @Size(max = 100) String name, @NotNull Unit unit) {}
