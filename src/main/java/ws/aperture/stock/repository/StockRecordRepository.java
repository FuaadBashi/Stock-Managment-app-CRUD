package ws.aperture.stock.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ws.aperture.stock.model.StockRecord;

public interface StockRecordRepository extends JpaRepository<StockRecord, Long> {
  List<StockRecord> findByStockItemIdOrderByIncomingDateDescIdDesc(Long id);

  boolean existsByStockItemId(Long id);
}
