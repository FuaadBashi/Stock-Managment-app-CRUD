package ws.aperture.stock.dto;

import java.util.List;

public record CustomerOrderRequestDTO(Long userId, List<CustomerOrderItemDTO> itemRequests ) {}


