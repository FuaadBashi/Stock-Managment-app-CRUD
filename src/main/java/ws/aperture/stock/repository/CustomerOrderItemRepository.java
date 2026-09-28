package ws.aperture.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ws.aperture.stock.model.CustomerOrderItem;
import ws.aperture.stock.model.keys.CustomerOrderItemId;

public interface CustomerOrderItemRepository
    extends JpaRepository<CustomerOrderItem, CustomerOrderItemId> {}
