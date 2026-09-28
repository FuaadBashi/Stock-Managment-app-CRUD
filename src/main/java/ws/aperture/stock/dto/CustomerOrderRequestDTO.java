package ws.aperture.stock.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CustomerOrderRequestDTO(
    @NotNull @Positive Long userId,
    @NotEmpty @Size(max = 100) List<@NotNull @Valid CustomerOrderItemDTO> itemRequests) {}
