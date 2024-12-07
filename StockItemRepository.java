package ws.aperture.stock.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import ws.aperture.stock.model.StockItem;


public interface StockItemRepository extends JpaRepository<StockItem, Long>  {}