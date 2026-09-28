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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.ProductAndStockStatus;
import ws.aperture.stock.enums.Unit;

@Entity
@Table(name = "stock_item")
@Getter
@Setter
public class StockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", unique = false)
    private Ingredient ingredient;

    @OneToMany(mappedBy = "stockItem")
    private Set<StockRecord> stockRecords;

    @Column(nullable = false, unique = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @PositiveOrZero
    @Column(nullable = true, unique = false)
    private double retailPrice;

    @PositiveOrZero
    @Column(nullable = true, unique = false)
    private double costPricePerUnit;

    @PositiveOrZero
    @Column(nullable = true, unique = false)
    private int openDurationDays;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private Unit unit = Unit.COUNT;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private ProductAndStockStatus status = ProductAndStockStatus.IN_STOCK;
}
