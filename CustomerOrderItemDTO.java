package ws.aperture.stock.dto;

import java.util.Set;
import java.util.stream.Collectors;

import ws.aperture.stock.model.CustomerOrderItem;

public record CustomerOrderItemDTO(Long productId, int quantity) {
    private static CustomerOrderItemDTO generateDTO( CustomerOrderItem soi ) {
        return new CustomerOrderItemDTO( soi.getProduct().getId(), soi.getQuantity());
    }

    public static Set<CustomerOrderItemDTO> generateDTOs( Set<CustomerOrderItem> sois ) {
        return sois
        .stream()
        .map( soi -> CustomerOrderItemDTO.generateDTO(soi) )
        .collect( Collectors.toSet() );
    }   
}