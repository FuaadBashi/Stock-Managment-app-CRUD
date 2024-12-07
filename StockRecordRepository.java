package ws.aperture.stock.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import ws.aperture.stock.model.StockRecord;

public interface StockRecordRepository extends JpaRepository<StockRecord, Long> {

}
    