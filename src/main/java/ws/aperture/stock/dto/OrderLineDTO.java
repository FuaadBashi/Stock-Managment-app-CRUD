package ws.aperture.stock.dto;

import java.math.BigDecimal;

public record OrderLineDTO(
    Long productId, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {}
