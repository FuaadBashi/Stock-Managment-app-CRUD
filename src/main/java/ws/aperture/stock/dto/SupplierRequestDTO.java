package ws.aperture.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SupplierRequestDTO(
    @NotBlank @Size(max = 100) String name,
    @NotBlank @Pattern(regexp = "[A-Za-z0-9]{8,12}") String companyNumber) {}
