package ws.aperture.stock.dto;

import java.time.LocalDateTime;
import java.util.Set;
import ws.aperture.stock.enums.CustomerOrderStatus;
import ws.aperture.stock.model.CustomerOrder;

public record CustomerOrderDTO(
        Long customerOrderId,
        Long creatorId,
        LocalDateTime orderTS,
        Set<CustomerOrderItemDTO> orderItems,
        CustomerOrderStatus status) {
    public static CustomerOrderDTO generateDTO(CustomerOrder customerOrder) {

        return new CustomerOrderDTO(
                customerOrder.getId(),
                customerOrder.getCreator().getId(),
                customerOrder.getOrderTimeStamp(),
                CustomerOrderItemDTO.generateDTOs(customerOrder.getCustomerOrderItems()),
                customerOrder.getStatus());
    }
}
