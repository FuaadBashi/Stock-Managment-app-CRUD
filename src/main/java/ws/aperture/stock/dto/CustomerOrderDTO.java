package ws.aperture.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import ws.aperture.stock.enums.CustomerOrderStatus;
import ws.aperture.stock.model.CustomerOrder;

public record CustomerOrderDTO(
    Long customerOrderId,
    Long creatorId,
    LocalDateTime orderTS,
    CustomerOrderStatus status,
    long version,
    List<OrderLineDTO> orderItems,
    BigDecimal total) {
  public static CustomerOrderDTO generateDTO(CustomerOrder o) {
    var lines =
        o.getCustomerOrderItems().stream()
            .sorted(java.util.Comparator.comparing(i -> i.getProduct().getId()))
            .map(
                i ->
                    new OrderLineDTO(
                        i.getProduct().getId(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
            .toList();
    return new CustomerOrderDTO(
        o.getId(),
        o.getCreator().getId(),
        o.getOrderTimeStamp(),
        o.getStatus(),
        o.getVersion(),
        lines,
        lines.stream().map(OrderLineDTO::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
  }
}
