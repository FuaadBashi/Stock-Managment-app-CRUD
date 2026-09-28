package ws.aperture.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.ConditionStatus;
import ws.aperture.stock.enums.Storage;

@Entity
@Table(name = "stock_record")
@Getter
@Setter
public class StockRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "stock_record_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "stock_item_id", nullable = false)
  private StockItem stockItem;

  @Column(nullable = false)
  private LocalDate incomingDate;

  @Column(nullable = false)
  private LocalDate expiryDate;

  @Column(nullable = false, precision = 14, scale = 3)
  private BigDecimal quantity;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ConditionStatus conditionStatus = ConditionStatus.SEALED;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Storage storage = Storage.ROOM_TEMP;
}
