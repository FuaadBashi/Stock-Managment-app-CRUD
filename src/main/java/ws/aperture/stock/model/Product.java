package ws.aperture.stock.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import ws.aperture.stock.enums.ProductAndStockStatus;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<CustomerOrderItem> customerOrderItems;

    // @ManyToMany
    // @JoinTable(name = "contains", joinColumns =  @JoinColumn(name = "product_id"),
    // inverseJoinColumns = @JoinColumn(name = "stock_item_id"))

    @OneToMany(mappedBy = "product")
    private Set<RecipeMapping> recipeMappings;

    @Column(nullable = false, unique = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @PositiveOrZero
    @Column(nullable = true, unique = false)
    private double retailPrice;

    @Column(nullable = true, unique = false)
    private String type;

    @Column(nullable = false, unique = false)
    @Enumerated(EnumType.STRING)
    private ProductAndStockStatus status = ProductAndStockStatus.IN_STOCK;
}
