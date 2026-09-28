package ws.aperture.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne
    @JoinColumn(name = "stock_item_id", nullable = false)
    private StockItem stockItem;

    @Column(nullable = false, unique = false)
    private LocalDate incomingDate;

    @Column(nullable = false, unique = false, columnDefinition = "DATE")
    private LocalDate expiryDate;

    @Column(nullable = true, unique = false, columnDefinition = "DATE")
    private LocalDate openDate;

    @Column(nullable = true, unique = false, columnDefinition = "DATE")
    private LocalDate useByDate;

    @Column(nullable = false, unique = false)
    private double quantity = 0.0;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private ConditionStatus conditionStatus;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private Storage storage;
}
